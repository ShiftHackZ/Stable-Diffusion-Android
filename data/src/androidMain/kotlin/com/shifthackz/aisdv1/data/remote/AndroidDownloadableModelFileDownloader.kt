package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.unzip
import com.shifthackz.aisdv1.domain.entity.DownloadState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Coordinates `AndroidDownloadableModelFileDownloader` behavior in the SDAI data layer.
 *
 * @throws IllegalStateException when the delegated operation cannot complete.
 * @author Dmitriy Moroz
 */
internal class AndroidDownloadableModelFileDownloader(
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI data layer.
     *
     * @throws IllegalStateException when the delegated operation cannot complete.
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : DownloadableModelFileDownloader {

    /**
     * Exposes the `httpClient` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: OkHttpClient = createDownloadHttpClient()

    /**
     * Executes the `download` step in the SDAI data layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @return Result produced by `download`.
     * @author Dmitriy Moroz
     */
    override fun download(
        id: String,
        url: String,
    ): Flow<DownloadState> = flow {
        val dir = File("${fileProviderDescriptor.localModelDirPath}/$id")
        val destination = File(getDestinationPath(id))
        if (destination.exists()) destination.delete()
        if (!dir.exists()) dir.mkdirs()

        emit(DownloadState.Downloading(0))
        try {
            val complete = downloadToFile(
                url = url,
                destination = destination,
                onProgress = { progress -> emit(DownloadState.Downloading(progress)) },
            )
            complete.unzip()
            complete.delete()
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
     * Loads SDAI data through `getDestinationPath`.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `getDestinationPath`.
     * @author Dmitriy Moroz
     */
    private fun getDestinationPath(id: String): String =
        "${fileProviderDescriptor.localModelDirPath}/$id/model.zip"

    /**
     * Executes the `downloadToFile` step in the SDAI data layer.
     *
     * @param url remote URL used by the operation.
     * @param destination destination value consumed by the API.
     * @param onProgress callback invoked by the component.
     * @return Result produced by `downloadToFile`.
     * @throws IllegalStateException when the delegated operation cannot complete.
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

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Failed to download model: HTTP ${response.code}")
            }

            val body = response.body ?: throw IllegalStateException(
                "Failed to download model: empty response body",
            )
            val totalBytes = body.contentLength()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var downloadedBytes = 0L
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

        return destination
    }

    /**
     * Provides the `companion object` singleton used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Creates the SDAI value produced by `createDownloadHttpClient`.
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
