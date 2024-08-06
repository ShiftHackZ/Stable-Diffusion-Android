package com.shifthackz.aisdv1.work.task

import android.content.Context
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.contract.RxDisposableContract
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.notification.PushNotificationManager
import com.shifthackz.aisdv1.work.Constants
import com.shifthackz.aisdv1.work.core.NotificationWorker
import com.shifthackz.aisdv1.work.mappers.toTextToImagePayload
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

internal class TextToImageTask(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
    private val preferenceManager: PreferenceManager,
    private val textToImageUseCase: TextToImageUseCase,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
) : NotificationWorker(context, workerParameters, pushNotificationManager), RxDisposableContract {

    override val compositeDisposable = CompositeDisposable()

    override val notificationId: Int = 5598

    override val genericNotificationId: Int = 5599

    private val source = preferenceManager.source

    override fun onStopped() {
        super.onStopped()
        compositeDisposable.clear()
    }

    override fun createWork(): Single<Result> {
        !observeHordeProcessStatusUseCase()
            .subscribeBy(::errorLog) { status ->
                debugLog("STUB", status)
                setForegroundNotification(
                    title = "Generating Text to Image ...",
                    body = "Queue position: ${status.queuePosition}\nWait time: ${status.waitTimeSeconds}",
                    silent = true,
                    canCancel = false,
                )
            }

        !observeLocalDiffusionProcessStatusUseCase()
            .subscribeBy(::errorLog) { status ->
                status.total
                debugLog("STUB", status)
                setForegroundNotification(
                    title = "Generating Text to Image ...",
                    body = "Processing step ${status.current} / ${status.total}",
                    silent = true,
                    progress = status.current to status.total,
                    canCancel = true,
                )
            }

        try {
            val payload = inputData.getByteArray(Constants.KEY_PAYLOAD)
                ?.toTextToImagePayload()
                ?: return Single.just(Result.failure())

            return textToImageUseCase(payload)
                .doOnSubscribe {
                    setForegroundNotification(
                        title = "Generating Text to Image ...",
                        body = "Please wait until generation is complete",
                        canCancel = source != ServerSource.LOCAL,
                    )
                }
                .map {
                    gen("Generation complete!")
                    Result.success()
                }
                .onErrorReturn { throwable ->
                    errorLog(throwable)
                    gen("Generation failed ;(")
                    Result.failure()
                }
                .doFinally {
                    compositeDisposable.clear()
                }
        } catch (e: Exception) {
            compositeDisposable.clear()
            return Single.just(Result.failure())
        }
    }
}
