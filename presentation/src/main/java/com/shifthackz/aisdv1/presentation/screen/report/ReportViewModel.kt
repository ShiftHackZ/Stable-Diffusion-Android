package com.shifthackz.aisdv1.presentation.screen.report

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.viewmodel.MviRxViewModel
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.domain.usecase.caching.GetLastResultFromCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.report.SendReportUseCase
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.navigation.router.main.MainRouter
import com.shifthackz.android.core.mvi.EmptyEffect
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy

class ReportViewModel(
    val itemId: Long,
    private val sendReportUseCase: SendReportUseCase,
    private val getGenerationResultUseCase: GetGenerationResultUseCase,
    private val getLastResultFromCacheUseCase: GetLastResultFromCacheUseCase,
    private val base64ToBitmapConverter: Base64ToBitmapConverter,
    private val mainRouter: MainRouter,
    private val schedulersProvider: SchedulersProvider,
    private val buildInfoProvider: BuildInfoProvider,
) : MviRxViewModel<ReportState, ReportIntent, EmptyEffect>() {

    override val initialState = ReportState()

    init {
        !getGenerationResult(itemId)
            .subscribeOnMainThread(schedulersProvider)
            .flatMap { ai ->
                base64ToBitmapConverter(Base64ToBitmapConverter.Input(ai.image))
                    .map(Base64ToBitmapConverter.Output::bitmap)
                    .map { bitmap -> ai.image to bitmap }
            }
            .subscribeBy(::errorLog) { (base64, bitmap) ->
                updateState { state ->
                    state.copy(
                        imageBitmap = bitmap,
                        imageBase64 = base64,
                        loading = false,
                    )
                }
            }
    }

    override fun processIntent(intent: ReportIntent) {
        when (intent) {
            ReportIntent.Submit -> submitReport()

            is ReportIntent.UpdateReason -> updateState {
                it.copy(reason = intent.reason)
            }

            is ReportIntent.UpdateText -> updateState {
                it.copy(text = intent.text)
            }

            ReportIntent.NavigateBack -> mainRouter.navigateBack()

            ReportIntent.DismissError -> updateState {
                it.copy(screenModal = Modal.None)
            }
        }
    }

    private fun getGenerationResult(id: Long): Single<AiGenerationResult> {
        if (id <= 0) return getLastResultFromCacheUseCase()
        return getGenerationResultUseCase(id)
    }

    private fun submitReport() = !sendReportUseCase(
        text = currentState.text,
        reason = currentState.reason,
        image = currentState.imageBase64,
    )
        .doOnSubscribe {
            updateState { it.copy(loading = true) }
        }
        .doFinally {
            updateState { it.copy(loading = false) }
        }
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(
            onError = { t ->
                errorLog(t)
                updateState { state ->
                    when (buildInfoProvider.type) {
                        BuildType.PLAY -> state.copy(reportSent = true)
                        else -> state.copy(
                            reportSent = false,
                            screenModal = Modal.Error(
                                (t.localizedMessage ?: t.message
                                ?: "Something went wrong").asUiText(),
                            )
                        )
                    }
                }
            },
            onComplete = {
                updateState { it.copy(reportSent = true) }
            },
        )
}
