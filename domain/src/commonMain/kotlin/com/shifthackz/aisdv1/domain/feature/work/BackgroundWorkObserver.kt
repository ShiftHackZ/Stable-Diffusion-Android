package com.shifthackz.aisdv1.domain.feature.work

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkResult
import com.shifthackz.aisdv1.domain.entity.BackgroundWorkStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Defines the `BackgroundWorkObserver` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface BackgroundWorkObserver {
    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    fun observeStatus(): Flow<BackgroundWorkStatus>
    /**
     * Loads SDAI data through `observeResult`.
     *
     * @return Result produced by `observeResult`.
     * @author Dmitriy Moroz
     */
    fun observeResult(): Flow<BackgroundWorkResult>
    /**
     * Executes the `dismissResult` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    fun dismissResult()
    /**
     * Executes the `refreshStatus` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    fun refreshStatus()
    /**
     * Executes the `postStatusMessage` step in the SDAI domain layer.
     *
     * @param title title value consumed by the API.
     * @param subTitle sub title value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun postStatusMessage(title: String, subTitle: String)
    /**
     * Executes the `postSuccessSignal` step in the SDAI domain layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun postSuccessSignal(result: List<AiGenerationResult>)
    /**
     * Executes the `postCancelSignal` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    fun postCancelSignal()
    /**
     * Executes the `postFailedSignal` step in the SDAI domain layer.
     *
     * @param t t value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun postFailedSignal(t: Throwable)
    /**
     * Executes the `hasActiveTasks` step in the SDAI domain layer.
     *
     * @return Result produced by `hasActiveTasks`.
     * @author Dmitriy Moroz
     */
    fun hasActiveTasks(): Boolean
}

/**
 * Provides the `NoOpBackgroundWorkObserver` singleton used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpBackgroundWorkObserver : BackgroundWorkObserver {
    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<BackgroundWorkStatus> = flowOf(BackgroundWorkStatus(false, "", ""))
    /**
     * Loads SDAI data through `observeResult`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeResult(): Flow<BackgroundWorkResult> = flowOf(BackgroundWorkResult.None)
    /**
     * Executes the `dismissResult` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun dismissResult() = Unit
    /**
     * Executes the `refreshStatus` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun refreshStatus() = Unit
    /**
     * Executes the `postStatusMessage` step in the SDAI domain layer.
     *
     * @param title title value consumed by the API.
     * @param subTitle sub title value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun postStatusMessage(title: String, subTitle: String) = Unit
    /**
     * Executes the `postSuccessSignal` step in the SDAI domain layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun postSuccessSignal(result: List<AiGenerationResult>) = Unit
    /**
     * Executes the `postCancelSignal` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun postCancelSignal() = Unit
    /**
     * Executes the `postFailedSignal` step in the SDAI domain layer.
     *
     * @param t t value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun postFailedSignal(t: Throwable) = Unit
    /**
     * Executes the `hasActiveTasks` step in the SDAI domain layer.
     *
     * @return Result produced by `hasActiveTasks`.
     * @author Dmitriy Moroz
     */
    override fun hasActiveTasks(): Boolean = false
}
