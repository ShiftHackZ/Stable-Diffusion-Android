package com.shifthackz.aisdv1.work.di

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.work.task.ImageToImageTask
import com.shifthackz.aisdv1.work.task.TextToImageTask

class SdaiWorkerFactory(
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val pushNotificationManager: PushNotificationManager,
    private val preferenceManager: PreferenceManager,
    private val textToImageUseCase: TextToImageUseCase,
    private val imageToImageUseCase: ImageToImageUseCase,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val activityIntentProvider: ActivityIntentProvider,
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
                activityIntentProvider = activityIntentProvider,
                backgroundWorkObserver = backgroundWorkObserver,
                preferenceManager = preferenceManager,
                textToImageUseCase = textToImageUseCase,
                observeHordeProcessStatusUseCase = observeHordeProcessStatusUseCase,
                observeLocalDiffusionProcessStatusUseCase = observeLocalDiffusionProcessStatusUseCase,
                interruptGenerationUseCase = interruptGenerationUseCase,
                fileProviderDescriptor = fileProviderDescriptor,
            )

            ImageToImageTask::class.java.name -> ImageToImageTask(
                context = appContext,
                workerParameters = workerParameters,
                pushNotificationManager = pushNotificationManager,
                activityIntentProvider = activityIntentProvider,
                backgroundWorkObserver = backgroundWorkObserver,
                preferenceManager = preferenceManager,
                imageToImageUseCase = imageToImageUseCase,
                observeHordeProcessStatusUseCase = observeHordeProcessStatusUseCase,
                observeLocalDiffusionProcessStatusUseCase = observeLocalDiffusionProcessStatusUseCase,
                interruptGenerationUseCase = interruptGenerationUseCase,
                fileProviderDescriptor = fileProviderDescriptor,
            )

            else -> null
        }
    }
}
