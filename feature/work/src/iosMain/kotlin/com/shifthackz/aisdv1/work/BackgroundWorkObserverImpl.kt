package com.shifthackz.aisdv1.work

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

/**
 * Implements `BackgroundWorkObserver` behavior for iOS background generation.
 *
 * @author Dmitriy Moroz
 */
internal class BackgroundWorkObserverImpl : BackgroundWorkObserver {

    private val stateFlow = MutableStateFlow(false)
    private val messageFlow = MutableStateFlow("" to "")
    private val resultFlow = MutableStateFlow<BackgroundWorkResult>(BackgroundWorkResult.None)

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<BackgroundWorkStatus> =
        combine(stateFlow, messageFlow) { running, (title, subTitle) ->
            BackgroundWorkStatus(running, title, subTitle)
        }

    /**
     * Loads SDAI data through `observeResult`.
     *
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
    override fun refreshStatus() = Unit

    /**
     * Executes the `postStatusMessage` step in the SDAI background work feature layer.
     *
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
     * @author Dmitriy Moroz
     */
    override fun hasActiveTasks(): Boolean = stateFlow.value
}
