package com.shifthackz.aisdv1.work

import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.work.di.WorkManagerProvider
import com.shifthackz.aisdv1.work.mappers.toByteArray
import com.shifthackz.aisdv1.work.task.ImageToImageTask
import com.shifthackz.aisdv1.work.task.TextToImageTask
import org.koin.java.KoinJavaComponent.inject
import java.io.File

/**
 * Implements `BackgroundTaskManager` behavior in the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class BackgroundTaskManagerImpl : BackgroundTaskManager {

    /**
     * Executes the `scheduleTextToImageTask` step in the SDAI background work feature layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override fun scheduleTextToImageTask(payload: TextToImagePayload) {
        runWork<TextToImageTask>(payload.toByteArray(), Constants.FILE_TEXT_TO_IMAGE)
    }

    /**
     * Executes the `scheduleImageToImageTask` step in the SDAI background work feature layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override fun scheduleImageToImageTask(payload: ImageToImagePayload) {
        runWork<ImageToImageTask>(payload.toByteArray(), Constants.FILE_IMAGE_TO_IMAGE)
    }

    /**
     * Executes the `retryLastTextToImageTask` step in the SDAI background work feature layer.
     *
     * @return Result produced by `retryLastTextToImageTask`.
     * @author Dmitriy Moroz
     */
    override fun retryLastTextToImageTask(): Result<Unit> {
        try {
            val bytes = readPayload(Constants.FILE_TEXT_TO_IMAGE)
                ?: return Result.failure(Throwable("Payload is null."))
            runWork<TextToImageTask>(bytes, Constants.FILE_TEXT_TO_IMAGE)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    /**
     * Executes the `retryLastImageToImageTask` step in the SDAI background work feature layer.
     *
     * @return Result produced by `retryLastImageToImageTask`.
     * @author Dmitriy Moroz
     */
    override fun retryLastImageToImageTask(): Result<Unit> {
        try {
            val bytes = readPayload(Constants.FILE_IMAGE_TO_IMAGE)
                ?: return Result.failure(Throwable("Payload is null."))
            runWork<ImageToImageTask>(bytes, Constants.FILE_IMAGE_TO_IMAGE)
            return Result.success(Unit)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    /**
     * Executes the `cancelAll` step in the SDAI background work feature layer.
     *
     * @return Result produced by `cancelAll`.
     * @author Dmitriy Moroz
     */
    override fun cancelAll(): Result<Unit> = runCatching {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        workManager().cancelAllWork()
    }

    /**
     * Executes the `runWork` step in the SDAI background work feature layer.
     *
     * @param bytes bytes value consumed by the API.
     * @param fileName file name value consumed by the API.
     * @author Dmitriy Moroz
     */
    private inline fun <reified W : ListenableWorker> runWork(bytes: ByteArray, fileName: String) {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        val workRequest = OneTimeWorkRequestBuilder<W>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag(Constants.TAG_GENERATION)
            .build()

        writePayload(bytes, fileName)
        workManager().cancelUniqueWork(Constants.TAG_GENERATION)
        workManager().enqueueUniqueWork(
            Constants.TAG_GENERATION,
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }

    /**
     * Loads SDAI data through `readPayload`.
     *
     * @param fileName file name value consumed by the API.
     * @return Result produced by `readPayload`.
     * @author Dmitriy Moroz
     */
    private fun readPayload(fileName: String): ByteArray? {
        val fileProviderDescriptor: FileProviderDescriptor by inject(FileProviderDescriptor::class.java)
        val cacheDirectory = File(fileProviderDescriptor.workCacheDirPath)
        if (!cacheDirectory.exists()) {
            return null
        }
        val outFile = File(cacheDirectory, fileName)
        return outFile.readBytes()
    }

    /**
     * Executes the `writePayload` step in the SDAI background work feature layer.
     *
     * @param bytes bytes value consumed by the API.
     * @param fileName file name value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun writePayload(bytes: ByteArray, fileName: String) {
        val fileProviderDescriptor: FileProviderDescriptor by inject(FileProviderDescriptor::class.java)
        val cacheDirectory = File(fileProviderDescriptor.workCacheDirPath)
        if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
        val outFile = File(cacheDirectory, fileName)
        if (!outFile.exists()) outFile.createNewFile()
        outFile.writeBytes(bytes)
    }
}
