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

/**
 * Coordinates `SdaiWorkerFactory` behavior in the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
class SdaiWorkerFactory(
    /**
     * Exposes the `backgroundWorkObserver` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val backgroundWorkObserver: BackgroundWorkObserver,
    /**
     * Exposes the `pushNotificationManager` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val pushNotificationManager: PushNotificationManager,
    /**
     * Exposes the `preferenceManager` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `textToImageUseCase` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val textToImageUseCase: TextToImageUseCase,
    /**
     * Exposes the `imageToImageUseCase` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageToImageUseCase: ImageToImageUseCase,
    /**
     * Exposes the `observeHordeProcessStatusUseCase` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    /**
     * Exposes the `observeLocalDiffusionProcessStatusUseCase` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    /**
     * Exposes the `interruptGenerationUseCase` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Exposes the `activityIntentProvider` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val activityIntentProvider: ActivityIntentProvider,
) : WorkerFactory() {

    /**
     * Creates the SDAI value produced by `createWorker`.
     *
     * @param appContext app context value consumed by the API.
     * @param workerClassName worker class name value consumed by the API.
     * @param workerParameters worker parameters value consumed by the API.
     * @return Result produced by `createWorker`.
     * @author Dmitriy Moroz
     */
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
