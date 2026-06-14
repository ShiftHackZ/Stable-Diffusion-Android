package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.unzip
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.NetworkUsageBucket
import com.shifthackz.aisdv1.domain.repository.NetworkUsageRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Downloads local model archives into Android app-private storage and reports real downloaded bytes.
 *
 * @param fileProviderDescriptor Provides the app-private local model directory path.
 * @param networkUsageRepository Persists downloaded byte deltas for network usage statistics.
 * @throws IllegalStateException when OkHttp returns a non-successful download response.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidDownloadableModelFileDownloader(
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
     * Long-running OkHttp client used for model archive downloads.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: OkHttpClient = createDownloadHttpClient()

    /**
     * Downloads a local model archive, unpacks zip files, and emits progress states.
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
    ): Flow<DownloadState> = flow {
        val dir = File("${fileProviderDescriptor.localModelDirPath}/$id")
        val destination = File(getDestinationPath(id, url))
        if (destination.exists()) destination.delete()
        if (!dir.exists()) dir.mkdirs()

        emit(DownloadState.Downloading(0))
        try {
            val complete = downloadToFile(
                url = url,
                destination = destination,
                onProgress = { progress -> emit(DownloadState.Downloading(progress)) },
            )
            if (complete.extension.equals(ZIP_EXTENSION, ignoreCase = true)) {
                complete.unzip()
                complete.delete()
            }
            emit(DownloadState.Complete(complete.path))
        } catch (e: CancellationException) {
            destination.delete()
            throw e
        } catch (e: Exception) {
            destination.delete()
            emit(DownloadState.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Resolves the concrete file path used before optional zip extraction.
     *
     * @param id Local model identifier that also names the destination directory.
     * @param url Remote URL whose last path segment is reused as the archive file name when present.
     * @return Absolute destination path under the local model directory.
     *
     * @author Dmitriy Moroz
     */
    private fun getDestinationPath(id: String, url: String): String {
        val fileName = url.toHttpUrlOrNull()
            ?.pathSegments
            ?.lastOrNull()
            ?.takeIf(String::isNotBlank)
            ?: DEFAULT_MODEL_ARCHIVE
        return "${fileProviderDescriptor.localModelDirPath}/$id/$fileName"
    }

    /**
     * Streams a remote model file to disk and records the exact received byte count.
     *
     * @param url Remote model archive URL.
     * @param destination File that receives the streamed response body.
     * @param onProgress Callback invoked with integer percent progress when content length is known.
     * @return The completed destination file.
     * @throws IllegalStateException when OkHttp returns a non-successful response.
     *
     * @author Dmitriy Moroz
     */
    private suspend fun downloadToFile(
        url: String,
        destination: File,
        onProgress: suspend (Int) -> Unit,
    ): File {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        var downloadedBytes = 0L
        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IllegalStateException("Failed to download model: HTTP ${response.code}")
                }

                val body = response.body
                val totalBytes = body.contentLength()
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var lastPercent = 0

                body.byteStream().use { inputStream ->
                    destination.outputStream().buffered().use { outputStream ->
                        while (true) {
                            currentCoroutineContext().ensureActive()
                            val bytes = inputStream.read(buffer)
                            if (bytes == -1) break

                            outputStream.write(buffer, 0, bytes)
                            downloadedBytes += bytes

                            if (totalBytes > 0L) {
                                val percent = ((downloadedBytes * 100L) / totalBytes)
                                    .toInt()
                                    .coerceIn(0, 100)
                                if (percent != lastPercent) {
                                    lastPercent = percent
                                    onProgress(percent)
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            networkUsageRepository.increment(NetworkUsageBucket.MODEL_DOWNLOADS, downloadedBytes)
        }

        return destination
    }

    /**
     * Download constants and client factory scoped to this downloader.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Creates an OkHttp client without a total call timeout for large model files.
         *
         * @author Dmitriy Moroz
         */
        fun createDownloadHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .callTimeout(0, TimeUnit.MILLISECONDS)
            .build()
    }
}

private const val ZIP_EXTENSION = "zip"
private const val DEFAULT_MODEL_ARCHIVE = "model.zip"
