package com.shifthackz.aisdv1.work.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.notification.PushNotificationManager
import com.shifthackz.aisdv1.work.task.TestNotificationTask
import com.shifthackz.aisdv1.work.task.TextToImageTask

class SdaiWorkerFactory(
    private val pushNotificationManager: PushNotificationManager,
    private val preferenceManager: PreferenceManager,
    private val textToImageUseCase: TextToImageUseCase,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TextToImageTask::class.java.name -> TextToImageTask(
                context = appContext,
                workerParameters = workerParameters,
                pushNotificationManager = pushNotificationManager,
                preferenceManager = preferenceManager,
                textToImageUseCase = textToImageUseCase,
                observeHordeProcessStatusUseCase = observeHordeProcessStatusUseCase,
                observeLocalDiffusionProcessStatusUseCase = observeLocalDiffusionProcessStatusUseCase,
            )

            TestNotificationTask::class.java.name -> TestNotificationTask(
                context = appContext,
                workerParameters = workerParameters,
                pushNotificationManager = pushNotificationManager,
            )

            else -> null
        }
    }
}
