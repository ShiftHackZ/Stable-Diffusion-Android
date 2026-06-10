package com.shifthackz.aisdv1.work

import androidx.work.WorkInfo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.work.di.WorkManagerProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.koin.java.KoinJavaComponent.inject

/**
 * Implements `BackgroundWorkObserver` behavior in the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class BackgroundWorkObserverImpl : BackgroundWorkObserver {

    /**
     * Exposes the `stateFlow` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val stateFlow = MutableStateFlow(false)
    /**
     * Exposes the `messageFlow` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val messageFlow = MutableStateFlow("" to "")
    /**
     * Exposes the `resultFlow` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val resultFlow = MutableStateFlow<BackgroundWorkResult>(BackgroundWorkResult.None)

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<BackgroundWorkStatus> {
        refreshStatus()
        return combine(stateFlow, messageFlow) { running, (title, subTitle) ->
            BackgroundWorkStatus(running, title, subTitle)
        }
    }

    /**
     * Loads SDAI data through `observeResult`.
     *
     * @return Result produced by `observeResult`.
     * @author Dmitriy Moroz
     */
    override fun observeResult(): Flow<BackgroundWorkResult> = resultFlow

    /**
     * Executes the `dismissResult` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun dismissResult() {
        resultFlow.value = BackgroundWorkResult.None
    }

    /**
     * Executes the `refreshStatus` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun refreshStatus() {
        stateFlow.value = hasActiveTasks()
    }

    /**
     * Executes the `postStatusMessage` step in the SDAI background work feature layer.
     *
     * @param title title value consumed by the API.
     * @param subTitle sub title value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun postStatusMessage(title: String, subTitle: String) {
        stateFlow.value = true
        messageFlow.value = title to subTitle
        resultFlow.value = BackgroundWorkResult.None
    }

    /**
     * Executes the `postSuccessSignal` step in the SDAI background work feature layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun postSuccessSignal(result: List<AiGenerationResult>) {
        stateFlow.value = false
        messageFlow.value = "" to ""
        resultFlow.value = BackgroundWorkResult.Success(result)
    }

    /**
     * Executes the `postCancelSignal` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    override fun postCancelSignal() {
        stateFlow.value = false
        messageFlow.value = "" to ""
        resultFlow.value = BackgroundWorkResult.None
    }

    /**
     * Executes the `postFailedSignal` step in the SDAI background work feature layer.
     *
     * @param t t value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun postFailedSignal(t: Throwable) {
        stateFlow.value = false
        messageFlow.value = "" to ""
        resultFlow.value = BackgroundWorkResult.Error(t)
    }

    /**
     * Executes the `hasActiveTasks` step in the SDAI background work feature layer.
     *
     * @return Result produced by `hasActiveTasks`.
     * @author Dmitriy Moroz
     */
    override fun hasActiveTasks(): Boolean {
        val workManager: WorkManagerProvider by inject(WorkManagerProvider::class.java)
        val workInfos = workManager().getWorkInfosByTag(Constants.TAG_GENERATION).get()
        val isRunning = workInfos.any { workInfo ->
            workInfo.state == WorkInfo.State.BLOCKED
                    || workInfo.state == WorkInfo.State.ENQUEUED
                    || workInfo.state == WorkInfo.State.RUNNING
        }
        return isRunning
    }
}
