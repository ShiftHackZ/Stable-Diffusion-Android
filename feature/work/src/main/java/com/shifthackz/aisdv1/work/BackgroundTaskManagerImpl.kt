package com.shifthackz.aisdv1.work

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.work.Constants.KEY_PAYLOAD
import com.shifthackz.aisdv1.work.di.WorkManagerProvider
import com.shifthackz.aisdv1.work.mappers.toByteArray
import com.shifthackz.aisdv1.work.task.TextToImageTask
import org.koin.java.KoinJavaComponent.inject

internal class BackgroundTaskManagerImpl : BackgroundTaskManager {

    override fun scheduleTextToImageTask(payload: TextToImagePayload) {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        val workRequest = OneTimeWorkRequestBuilder<TextToImageTask>()
            .setInputData(workDataOf(KEY_PAYLOAD to payload.toByteArray()))
            .build()

//        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
//        val workRequest = OneTimeWorkRequestBuilder<TestNotificationTask>().build()

        workManager().enqueue(workRequest)
    }
}
