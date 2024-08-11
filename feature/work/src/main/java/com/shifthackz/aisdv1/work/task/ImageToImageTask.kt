package com.shifthackz.aisdv1.work.task

import android.content.Context
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
import com.shifthackz.aisdv1.work.Constants
import com.shifthackz.aisdv1.work.Constants.NOTIFICATION_IMAGE_TO_IMAGE_FOREGROUND
import com.shifthackz.aisdv1.work.Constants.NOTIFICATION_IMAGE_TO_IMAGE_GENERIC
import com.shifthackz.aisdv1.work.core.CoreGenerationWorker
import com.shifthackz.aisdv1.work.mappers.toImageToImagePayload
import io.reactivex.rxjava3.core.Single
import java.io.File

internal class ImageToImageTask(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
    activityIntentProvider: ActivityIntentProvider,
    preferenceManager: PreferenceManager,
    observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    interruptGenerationUseCase: InterruptGenerationUseCase,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val imageToImageUseCase: ImageToImageUseCase,
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

    override val notificationId = NOTIFICATION_IMAGE_TO_IMAGE_FOREGROUND

    override val genericNotificationId = NOTIFICATION_IMAGE_TO_IMAGE_GENERIC

    override fun createWork(): Single<Result> {
        handleStart()
        backgroundWorkObserver.refreshStatus()
        backgroundWorkObserver.dismissResult()
        listenHordeStatus()
        listenLocalDiffusionStatus()
        return try {
            val file = File(fileProviderDescriptor.workCacheDirPath, Constants.FILE_IMAGE_TO_IMAGE)
            if (!file.exists()) {
                handleError(Throwable("File is null."))
                compositeDisposable.clear()
                return Single.just(Result.failure())
            }

            val bytes = file.readBytes()
            val payload = bytes.toImageToImagePayload()

            if (payload == null) {
                handleError(Throwable("Payload is null."))
                compositeDisposable.clear()
                return Single.just(Result.failure())
            }

            listenHordeStatus()

            imageToImageUseCase(payload)
                .doOnSubscribe { handleProcess() }
                .map { result ->
                    handleSuccess(result)
                    Result.success()
                }
                .onErrorReturn { t ->
                    handleError(t)
                    Result.failure()
                }
                .doFinally { compositeDisposable.clear() }
        } catch (e: Exception) {
            handleError(e)
            compositeDisposable.clear()
            Single.just(Result.failure())
        }
    }
}
