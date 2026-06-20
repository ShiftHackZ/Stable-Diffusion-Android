@file:Suppress("MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.work.core

import android.content.Context
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.localization.formatter.DurationFormatter
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveBonsaiProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

/**
 * Shared foreground worker base for generation tasks.
 *
 * It owns cancellation, progress subscriptions, foreground notifications, and
 * background observer updates that are common to txt2img and img2img work.
 */
internal abstract class CoreGenerationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    pushNotificationManager: PushNotificationManager,
    activityIntentProvider: ActivityIntentProvider,
    private val preferenceManager: PreferenceManager,
    private val backgroundWorkObserver: BackgroundWorkObserver,
    private val observeHordeProcessStatusUseCase: ObserveHordeProcessStatusUseCase,
    private val observeLocalDiffusionProcessStatusUseCase: ObserveLocalDiffusionProcessStatusUseCase,
    private val observeBonsaiProcessStatusUseCase: ObserveBonsaiProcessStatusUseCase,
    private val interruptGenerationUseCase: InterruptGenerationUseCase,
) : NotificationWorker(
    context = context,
    workerParameters = workerParameters,
    pushNotificationManager = pushNotificationManager,
    activityIntentProvider = activityIntentProvider,
) {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    protected val source = preferenceManager.source

    protected suspend fun handleCancellation() {
        clearSubscriptions()
        runCatching { interruptGenerationUseCase() }
            .onFailure(::errorLog)
        backgroundWorkObserver.postCancelSignal()
    }

    protected fun clearSubscriptions() {
        coroutineScope.coroutineContext.cancelChildren()
    }

    protected fun listenSourceStatus() {
        when (source) {
            ServerSource.HORDE -> listenHordeStatus()
            ServerSource.LOCAL_MICROSOFT_ONNX -> listenLocalDiffusionStatus()
            ServerSource.LOCAL_APPLE_BONSAI -> listenBonsaiStatus()
            else -> Unit
        }
    }

    protected fun listenHordeStatus() {
        coroutineScope.launch {
            observeHordeProcessStatusUseCase()
                .catch { t -> errorLog(t) }
                .collect { status ->
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
        }
    }

    protected fun listenLocalDiffusionStatus() {
        coroutineScope.launch {
            observeLocalDiffusionProcessStatusUseCase()
                .catch { t -> errorLog(t) }
                .collect { status ->
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
                        canCancel = preferenceManager.localOnnxAllowCancel,
                    )
                }
        }
    }

    protected fun listenBonsaiStatus() {
        coroutineScope.launch {
            observeBonsaiProcessStatusUseCase()
                .catch { t -> errorLog(t) }
                .collect { status ->
                    val title = applicationContext.getString(LocalizationR.string.notification_running_title)
                    val subTitle = applicationContext.getString(
                        LocalizationR.string.communicating_status_steps,
                        status.current.toString(),
                        status.total.toString(),
                    )
                    backgroundWorkObserver.postStatusMessage(title, subTitle)
                    setForegroundNotification(
                        title = title,
                        body = subTitle,
                        silent = true,
                        progress = status.current to status.total,
                        canCancel = true,
                    )
                }
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
            canCancel = source != ServerSource.LOCAL_MICROSOFT_ONNX,
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
