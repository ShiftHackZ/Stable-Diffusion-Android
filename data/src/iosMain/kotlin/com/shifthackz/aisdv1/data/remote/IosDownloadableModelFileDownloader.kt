package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.NetworkUsageBucket
import com.shifthackz.aisdv1.domain.repository.NetworkUsageRepository
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSURL
import platform.Foundation.NSURLResponse
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionDownloadDelegateProtocol
import platform.Foundation.NSURLSessionDownloadTask
import platform.Foundation.NSURLSessionTask
import platform.darwin.NSObject

/**
 * Downloads local model archives into iOS app storage and reports received bytes.
 *
 * @param fileProviderDescriptor Provides the app-private local model directory path.
 * @param networkUsageRepository Persists downloaded byte deltas for network usage statistics.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosDownloadableModelFileDownloader(
    /**
     * Provides the app-private local model directory path.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Persists downloaded byte deltas for network usage statistics.
     *
     * @author Dmitriy Moroz
     */
    private val networkUsageRepository: NetworkUsageRepository,
) : DownloadableModelFileDownloader {

    /**
     * Starts an NSURLSession download and emits model download progress.
     *
     * @param id Local model identifier that also names the destination directory.
     * @param url Remote model archive URL.
     * @return Download progress, completion, or error state stream.
     *
     * @author Dmitriy Moroz
     */
    override fun download(
        id: String,
        url: String,
    ): Flow<DownloadState> = callbackFlow {
        trySend(DownloadState.Downloading(0))
        val modelDir = "${fileProviderDescriptor.localModelDirPath}/$id"
        val destinationPath = "$modelDir/$MODEL_ARCHIVE_NAME"
        val temporaryPath = "$destinationPath$DOWNLOAD_FILE_SUFFIX"
        val fileManager = NSFileManager.defaultManager

        fileManager.createDirectoryAtPath(
            path = modelDir,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
        fileManager.deleteDownloadFiles(destinationPath, temporaryPath)

        val downloadUrl = NSURL.URLWithString(url)
        if (downloadUrl == null) {
            trySend(DownloadState.Error(IllegalStateException("Invalid model download URL.")))
            close()
            return@callbackFlow
        }

        lateinit var session: NSURLSession
        var finished = false
        val delegate = IosModelDownloadDelegate(
            destinationPath = temporaryPath,
            onProgress = { progress -> trySend(DownloadState.Downloading(progress)) },
            onBytesReceived = { bytes ->
                networkUsageRepository.enqueueIncrement(NetworkUsageBucket.MODEL_DOWNLOADS, bytes)
            },
            onComplete = { result ->
                finished = true
                result.fold(
                    onSuccess = {
                        if (fileManager.moveItemAtPath(
                                srcPath = temporaryPath,
                                toPath = destinationPath,
                                error = null,
                            )
                        ) {
                            trySend(DownloadState.Downloading(100))
                            trySend(DownloadState.Complete(destinationPath))
                        } else {
                            fileManager.deleteDownloadFiles(destinationPath, temporaryPath)
                            trySend(
                                DownloadState.Error(
                                    IllegalStateException("Failed to finalize downloaded model file."),
                                ),
                            )
                        }
                        session.finishTasksAndInvalidate()
                        close()
                    },
                    onFailure = { error ->
                        fileManager.deleteDownloadFiles(destinationPath, temporaryPath)
                        trySend(DownloadState.Error(error))
                        session.invalidateAndCancel()
                        close()
                    },
                )
            },
        )
        session = NSURLSession.sessionWithConfiguration(
            configuration = modelDownloadSessionConfiguration(),
            delegate = delegate,
            delegateQueue = null,
        )
        val task = session.downloadTaskWithURL(downloadUrl)
        task.resume()

        awaitClose {
            if (!finished) {
                task.cancel()
                session.invalidateAndCancel()
                fileManager.deleteDownloadFiles(destinationPath, temporaryPath)
            }
        }
    }.flowOn(Dispatchers.Default)

    /**
     * Download file names used while moving the completed NSURLSession temporary file into place.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        const val DOWNLOAD_FILE_SUFFIX = ".download"
        const val MODEL_ARCHIVE_NAME = "model.zip"
    }
}

/**
 * Creates a non-persistent session for large model archive downloads.
 *
 * Default iOS sessions can leave completed download artifacts under `Library/Caches`, which makes
 * a model archive look like app cache until the user clears it manually.
 *
 * @return NSURLSession configuration that avoids persistent URL cache writes for model downloads.
 *
 * @author Dmitriy Moroz
 */
private fun modelDownloadSessionConfiguration(): NSURLSessionConfiguration =
    NSURLSessionConfiguration.ephemeralSessionConfiguration()

/**
 * NSURLSession delegate that converts native progress callbacks into app download state callbacks.
 *
 * @param destinationPath Temporary destination where NSURLSession output is moved before finalization.
 * @param onProgress Emits integer percent progress when iOS reports total expected byte count.
 * @param onBytesReceived Emits every positive byte delta for traffic statistics.
 * @param onComplete Completes the callbackFlow with success or a user-visible download error.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
private class IosModelDownloadDelegate(
    private val destinationPath: String,
    private val onProgress: (Int) -> Unit,
    private val onBytesReceived: (Long) -> Unit,
    private val onComplete: (Result<Unit>) -> Unit,
) : NSObject(), NSURLSessionDownloadDelegateProtocol {

    private var lastPercent = 0
    private var completed = false

    override fun URLSession(
        session: NSURLSession,
        downloadTask: NSURLSessionDownloadTask,
        didWriteData: Long,
        totalBytesWritten: Long,
        totalBytesExpectedToWrite: Long,
    ) {
        if (didWriteData > 0L) {
            onBytesReceived(didWriteData)
        }
        if (totalBytesExpectedToWrite <= 0L) return

        val percent = ((totalBytesWritten * 100L) / totalBytesExpectedToWrite)
            .toInt()
            .coerceIn(0, 100)
        if (percent != lastPercent) {
            lastPercent = percent
            onProgress(percent)
        }
    }

    override fun URLSession(
        session: NSURLSession,
        downloadTask: NSURLSessionDownloadTask,
        didFinishDownloadingToURL: NSURL,
    ) {
        if (completed) return

        val httpError = downloadTask.response.httpError()
        if (httpError != null) {
            complete(Result.failure(httpError))
            return
        }

        val sourcePath = didFinishDownloadingToURL.path
        if (sourcePath == null) {
            complete(
                Result.failure(
                    IllegalStateException("Failed to download model: empty temporary file path."),
                ),
            )
            return
        }

        NSFileManager.defaultManager.removeItemAtPath(path = destinationPath, error = null)
        if (NSFileManager.defaultManager.moveItemAtPath(
                srcPath = sourcePath,
                toPath = destinationPath,
                error = null,
            )
        ) {
            complete(Result.success(Unit))
        } else {
            complete(Result.failure(IllegalStateException("Failed to save downloaded model file.")))
        }
    }

    override fun URLSession(
        session: NSURLSession,
        task: NSURLSessionTask,
        didCompleteWithError: NSError?,
    ) {
        if (completed || didCompleteWithError == null) return

        complete(Result.failure(IllegalStateException(didCompleteWithError.localizedDescription)))
    }

    /**
     * Completes the delegate exactly once even when NSURLSession reports multiple terminal events.
     *
     * @param result Download finalization result propagated to the callbackFlow.
     *
     * @author Dmitriy Moroz
     */
    private fun complete(result: Result<Unit>) {
        if (completed) return
        completed = true
        onComplete(result)
    }
}

/**
 * Converts non-2xx HTTP download responses into the same exception shape as Android downloads.
 *
 * @receiver Optional NSURL response produced by the download task.
 *
 * @author Dmitriy Moroz
 */
private fun NSURLResponse?.httpError(): IllegalStateException? {
    val statusCode = (this as? NSHTTPURLResponse)?.statusCode?.toInt() ?: return null
    return if (statusCode in 200..299) {
        null
    } else {
        IllegalStateException("Failed to download model: HTTP $statusCode")
    }
}

/**
 * Removes stale final and temporary model archive files before retrying or after cancellation.
 *
 * @receiver File manager that owns the app storage mutation.
 * @param destinationPath Final model archive path.
 * @param temporaryPath Temporary download path used before finalization.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
private fun NSFileManager.deleteDownloadFiles(
    destinationPath: String,
    temporaryPath: String,
) {
    removeItemAtPath(path = destinationPath, error = null)
    removeItemAtPath(path = temporaryPath, error = null)
}
