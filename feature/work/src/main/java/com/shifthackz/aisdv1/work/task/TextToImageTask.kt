package com.shifthackz.aisdv1.work.task

import android.content.Context
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.work.Constants
import com.shifthackz.aisdv1.work.Constants.NOTIFICATION_TEXT_TO_IMAGE_FOREGROUND
import com.shifthackz.aisdv1.work.Constants.NOTIFICATION_TEXT_TO_IMAGE_GENERIC
import com.shifthackz.aisdv1.work.core.CoreGenerationWorker
import com.shifthackz.aisdv1.work.mappers.toTextToImagePayload
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class TextToImageTask(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
    activityIntentProvider: ActivityIntentProvider,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    interruptGenerationUseCase: InterruptGenerationUseCase,
    private val preferenceManager: PreferenceManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val textToImageUseCase: TextToImageUseCase,
    private val fileProviderDescriptor: FileProviderDescriptor,
) : CoreGenerationWorker(
    context = context,
    workerParameters = workerParameters,
    pushNotificationManager = pushNotificationManager,
    activityIntentProvider = activityIntentProvider,
    preferenceManager = preferenceManager,
    backgroundWorkObserver = backgroundWorkObserver,
    observeHordeProcessStatusUseCase = observeHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase = observeLocalDiffusionProcessStatusUseCase,
    interruptGenerationUseCase = interruptGenerationUseCase,
) {

    override val notificationId: Int = NOTIFICATION_TEXT_TO_IMAGE_FOREGROUND

    override val genericNotificationId: Int = NOTIFICATION_TEXT_TO_IMAGE_GENERIC

    override fun createWork(): Single<Result> {
        // Workaround for LocalDiffusion provider:
        //
        // If LocalDiffusion process previously died, prevent WorkManager to go to infinite
        // task repeat loop.
        if (preferenceManager.backgroundProcessCount > 0) {
            handleProcess()
            handleError(Throwable("Background process count > 0"))
            compositeDisposable.clear()
            preferenceManager.backgroundProcessCount = 0
            debugLog("Background process count > 0! Skipping task.")
            return Single.just(Result.failure())
        }

        preferenceManager.backgroundProcessCount++
        handleStart()
        backgroundWorkObserver.refreshStatus()
        backgroundWorkObserver.dismissResult()
        debugLog("Starting TextToImageTask!")

        return try {
            val file = File(fileProviderDescriptor.workCacheDirPath, Constants.FILE_TEXT_TO_IMAGE)
            if (!file.exists()) {
                preferenceManager.backgroundProcessCount--
                val t = Throwable("File is null.")
                handleError(t)
                compositeDisposable.clear()
                errorLog(t, "Payload file does not exist.")
                return Single.just(Result.failure())
            }

            val bytes = file.readBytes()
            val payload = bytes.toTextToImagePayload()

            if (payload == null) {
                preferenceManager.backgroundProcessCount--
                val t = Throwable("Payload is null.")
                handleError(t)
                compositeDisposable.clear()
                errorLog(t, "Payload was failed to read/parse.")
                return Single.just(Result.failure())
            }

            listenHordeStatus()
            listenLocalDiffusionStatus()

            textToImageUseCase(payload)
                .doOnSubscribe { handleProcess() }
                .map { result ->
                    preferenceManager.backgroundProcessCount--
                    handleSuccess(result)
                    debugLog("Generation finished successfully!")
                    Result.success()
                }
                .onErrorReturn { t ->
                    preferenceManager.backgroundProcessCount--
                    handleError(t)
                    errorLog(t, "Caught exception from TextToImageUseCase!")
                    Result.failure()
                }
                .doFinally { compositeDisposable.clear() }
        } catch (e: Exception) {
            preferenceManager.backgroundProcessCount--
            handleError(e)
            compositeDisposable.clear()
            errorLog(e, "Caught exception from TextToImageTask worker!")
            Single.just(Result.failure())
        }
    }
}
