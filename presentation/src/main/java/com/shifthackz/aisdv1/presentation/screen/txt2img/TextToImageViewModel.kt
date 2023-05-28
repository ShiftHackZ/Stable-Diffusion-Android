package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.schedulers.subscribeOnMainThread
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.usecase.coin.ObserveCoinsUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.presentation.core.GenerationMviViewModel
import com.shifthackz.aisdv1.presentation.features.AiImageGenerated
import io.reactivex.rxjava3.kotlin.subscribeBy

class TextToImageViewModel(
    getStableDiffusionSamplersUseCase: GetStableDiffusionSamplersUseCase,
    buildInfoProvider: BuildInfoProvider,
    observeCoinsUseCase: ObserveCoinsUseCase,
    private val textToImageUseCase: TextToImageUseCase,
    private val saveGenerationResultUseCase: SaveGenerationResultUseCase,
    private val schedulersProvider: SchedulersProvider,
    private val dimensionValidator: DimensionValidator,
    private val preferenceManager: PreferenceManager,
    private val analytics: Analytics,
) : GenerationMviViewModel<TextToImageState, EmptyEffect>(
    buildInfoProvider,
    preferenceManager,
    observeCoinsUseCase,
    getStableDiffusionSamplersUseCase,
    schedulersProvider,
) {

    override val emptyState = TextToImageState()

    override fun setState(state: TextToImageState) = super.setState(
        state.copy(
            widthValidationError = dimensionValidator(state.width).mapToUi(),
            heightValidationError = dimensionValidator(state.height).mapToUi(),
        )
    )

    fun dismissScreenDialog() = setActiveDialog(TextToImageState.Dialog.None)

    fun generate() {
        if (!currentState.generateButtonEnabled) {
            setActiveDialog(TextToImageState.Dialog.NoSdAiCoins)
            return
        }
        !currentState
            .mapToPayload()
            .let(textToImageUseCase::invoke)
            .doOnSubscribe { setActiveDialog(TextToImageState.Dialog.Communicating) }
            .subscribeOnMainThread(schedulersProvider)
            .subscribeBy(
                onError = { t ->
                    setActiveDialog(
                        TextToImageState.Dialog.Error(
                            (t.localizedMessage ?: "Something went wrong").asUiText()
                        )
                    )
                    errorLog(t)
                },
                onSuccess = { ai ->
                    analytics.logEvent(AiImageGenerated(ai))
                    setActiveDialog(
                        TextToImageState.Dialog.Image(ai, preferenceManager.autoSaveAiResults)
                    )
                },
            )
    }

    fun saveGeneratedResult(ai: AiGenerationResult) = !saveGenerationResultUseCase(ai)
        .subscribeOnMainThread(schedulersProvider)
        .subscribeBy(::errorLog) { dismissScreenDialog() }

    private fun setActiveDialog(dialog: TextToImageState.Dialog) = currentState
        .copy(screenDialog = dialog)
        .let(::setState)
}
