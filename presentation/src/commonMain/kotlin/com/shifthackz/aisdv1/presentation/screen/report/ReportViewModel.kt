package com.shifthackz.aisdv1.presentation.screen.report

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.report.SendReportUseCase
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.navigation.router.ReportRouter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

/**
 * Coordinates `ReportViewModel` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class ReportViewModel(
    /**
     * Exposes the `itemId` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val itemId: Long,
    /**
     * Exposes the `dispatchersProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val dispatchersProvider: DispatchersProvider,
    /**
     * Exposes the `sendReportUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val sendReportUseCase: SendReportUseCase,
    /**
     * Exposes the `getGenerationResultUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getGenerationResultUseCase: GetGenerationResultUseCase,
    /**
     * Exposes the `getLastResultFromCacheUseCase` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val getLastResultFromCacheUseCase: GetLastResultFromCacheUseCase,
    /**
     * Exposes the `router` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val router: ReportRouter,
    /**
     * Exposes the `buildInfoProvider` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val buildInfoProvider: BuildInfoProvider,
    /**
     * Exposes the `onError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<ReportState, ReportIntent, EmptyEffect>(
    initialState = ReportState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.io) {
            runCatching { getGenerationResult(itemId) }
                .onFailure(onError)
                .onSuccess { ai ->
                    updateState {
                        it.copy(
                            imageBase64 = ai.image,
                            loading = false,
                        )
                    }
                }
        }
    }

    override fun processIntent(intent: ReportIntent) {
        when (intent) {
            ReportIntent.Submit -> submitReport()
            is ReportIntent.UpdateReason -> updateState { it.copy(reason = intent.reason) }
            is ReportIntent.UpdateText -> updateState { it.copy(text = intent.text) }
            ReportIntent.NavigateBack -> router.navigateBack()
            ReportIntent.DismissError -> updateState { it.copy(error = ErrorState.None) }
        }
    }

    private suspend fun getGenerationResult(id: Long): AiGenerationResult {
        if (id <= 0) return getLastResultFromCacheUseCase()
        return getGenerationResultUseCase(id)
    }

    private fun submitReport() {
        updateState { it.copy(loading = true) }
        launch(dispatchersProvider.io) {
            try {
                withTimeout(REPORT_TIMEOUT_MS) {
                    sendReportUseCase(
                        text = currentState.text,
                        reason = currentState.reason,
                        image = currentState.imageBase64,
                    )
                }
                updateState { it.copy(reportSent = true) }
            } catch (t: Throwable) {
                if (t is CancellationException && t !is TimeoutCancellationException) throw t
                onError(t)
                updateState { state ->
                    when (buildInfoProvider.type) {
                        BuildType.PLAY -> state.copy(reportSent = true)
                        else -> state.copy(
                            reportSent = false,
                            error = ErrorState.WithMessage(
                                (t.message ?: "Something went wrong").asUiText(),
                            ),
                        )
                    }
                }
            } finally {
                updateState { it.copy(loading = false) }
            }
        }
    }

    companion object {
        private const val REPORT_TIMEOUT_MS = 10_000L
    }
}
