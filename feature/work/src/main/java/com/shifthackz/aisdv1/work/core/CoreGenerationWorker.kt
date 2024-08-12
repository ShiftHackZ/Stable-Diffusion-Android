@file:Suppress("MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.work.core

import android.content.Context
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.contract.RxDisposableContract
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.localization.formatter.DurationFormatter
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

internal abstract class CoreGenerationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
    activityIntentProvider: ActivityIntentProvider,
    private val preferenceManager: PreferenceManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
) : NotificationWorker(
    context = context,
    workerParameters = workerParameters,
    pushNotificationManager = pushNotificationManager,
    activityIntentProvider = activityIntentProvider,
), RxDisposableContract {

    override val compositeDisposable = CompositeDisposable()

    protected val source = preferenceManager.source

    override fun onStopped() {
        super.onStopped()
        runCatching {
            interruptGenerationUseCase()
                .onErrorComplete()
                .blockingAwait()
        }
        compositeDisposable.clear()
        backgroundWorkObserver.postCancelSignal()
    }

    protected fun listenHordeStatus() = !observeHordeProcessStatusUseCase()
        .subscribeBy(::errorLog) { status ->
            val title = applicationContext.getString(LocalizationR.string.notification_running_title)
            val subTitle = buildString {
                appendLine(
                    applicationContext.getString(
                        LocalizationR.string.communicating_status_queue,
                        "${status.queuePosition}",
                    ),
                )
                append(
                    applicationContext.getString(
                        LocalizationR.string.communicating_wait_time,
                        DurationFormatter.formatDurationInSeconds(status.waitTimeSeconds),
                    ),
                )
            }
            backgroundWorkObserver.postStatusMessage(title, subTitle)
            setForegroundNotification(
                title = title,
                body = subTitle,
                silent = true,
                canCancel = true,
            )
        }

    protected fun listenLocalDiffusionStatus() {
        !observeLocalDiffusionProcessStatusUseCase()
            .subscribeBy(::errorLog) { status ->
                val title = applicationContext.getString(LocalizationR.string.notification_running_title)
                val subTitle = applicationContext.getString(
                    LocalizationR.string.communicating_status_steps,
                    status.current.toString(),
                    status.total.toString()
                )
                backgroundWorkObserver.postStatusMessage(title, subTitle)
                setForegroundNotification(
                    title = title,
                    body = subTitle,
                    silent = true,
                    progress = status.current to status.total,
                    canCancel = preferenceManager.localDiffusionAllowCancel,
                )
            }
    }

    protected fun handleStart() {
        val title = applicationContext.getString(LocalizationR.string.notification_started_title)
        val subTitle = applicationContext.getString(LocalizationR.string.notification_running_sub_title)
        showGenericNotification(title, subTitle)
        handleProcess()
    }

    protected fun handleProcess() {
        val title = applicationContext.getString(LocalizationR.string.notification_running_title)
        val subTitle = applicationContext.getString(LocalizationR.string.notification_running_sub_title)
        backgroundWorkObserver.postStatusMessage(title, subTitle)
        setForegroundNotification(
            title = title,
            body = subTitle,
            canCancel = source != ServerSource.LOCAL,
        )
    }

    protected fun handleSuccess(result: List<AiGenerationResult>) {
        backgroundWorkObserver.refreshStatus()
        backgroundWorkObserver.postSuccessSignal(result)
        val title = applicationContext.getString(LocalizationR.string.notification_finish_title)
        val subTitle = applicationContext.getString(LocalizationR.string.notification_finish_sub_title)
        showGenericNotification(title, subTitle)
    }

    protected fun handleError(t: Throwable) {
        backgroundWorkObserver.postFailedSignal(t)
        val title = applicationContext.getString(LocalizationR.string.notification_fail_title)
        val subTitle = applicationContext.getString(LocalizationR.string.notification_fail_sub_title)
        showGenericNotification(title, subTitle)
    }
}
