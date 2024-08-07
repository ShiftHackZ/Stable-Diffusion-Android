package com.shifthackz.aisdv1.work

import androidx.work.OneTimeWorkRequestBuilder
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

internal class BackgroundTaskManagerImpl : BackgroundTaskManager {

    override fun scheduleTextToImageTask(payload: TextToImagePayload) {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        val workRequest = OneTimeWorkRequestBuilder<TextToImageTask>()
            .setInitialRunAttemptCount(1)
            .addTag(Constants.TAG_GENERATION)
            .build()

        writePayload(payload.toByteArray(), Constants.FILE_TEXT_TO_IMAGE)
        workManager().cancelAllWork()
        workManager().enqueue(workRequest)
    }

    override fun scheduleImageToImageTask(payload: ImageToImagePayload) {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        val workRequest = OneTimeWorkRequestBuilder<ImageToImageTask>()
            .setInitialRunAttemptCount(1)
            .addTag(Constants.TAG_GENERATION)
            .build()

        writePayload(payload.toByteArray(), Constants.FILE_IMAGE_TO_IMAGE)
        workManager().cancelAllWork()
        workManager().enqueue(workRequest)
    }

    private fun writePayload(bytes: ByteArray, fileName: String) {
        val fileProviderDescriptor: FileProviderDescriptor by inject(FileProviderDescriptor::class.java)
        val cacheDirectory = File(fileProviderDescriptor.workCacheDirPath)
        if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
        val outFile = File(cacheDirectory, fileName)
        if (!outFile.exists()) outFile.createNewFile()
        outFile.writeBytes(bytes)
    }
}
