package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.OpenAiStyle
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState

abstract class GenerationMviState : MviState {
    abstract val onBoardingDemo: Boolean
    abstract val screenModal: Modal
    abstract val mode: ServerSource
    abstract val advancedToggleButtonVisible: Boolean
    abstract val advancedOptionsVisible: Boolean
    abstract val formPromptTaggedInput: Boolean
    abstract val prompt: String
    abstract val negativePrompt: String
    abstract val width: String
    abstract val height: String
    abstract val samplingSteps: Int
    abstract val cfgScale: Float
    abstract val restoreFaces: Boolean
    abstract val seed: String
    abstract val subSeed: String
    abstract val subSeedStrength: Float
    abstract val selectedSampler: String
    abstract val availableSamplers: List<String>
    abstract val selectedStylePreset: StabilityAiStylePreset
    abstract val selectedClipGuidancePreset: StabilityAiClipGuidance
    abstract val openAiModel: OpenAiModel
    abstract val openAiSize: OpenAiSize
    abstract val openAiQuality: OpenAiQuality
    abstract val openAiStyle: OpenAiStyle
    abstract val widthValidationError: UiText?
    abstract val heightValidationError: UiText?
    abstract val nsfw: Boolean
    abstract val batchCount: Int
    abstract val generateButtonEnabled: Boolean

    open val promptKeywords: List<String>
        get() = prompt.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    open val negativePromptKeywords: List<String>
        get() = negativePrompt.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    open val hasValidationErrors: Boolean
        get() = widthValidationError != null || heightValidationError != null

    open fun copyState(
        onBoardingDemo: Boolean = this.onBoardingDemo,
        screenModal: Modal = this.screenModal,
        mode: ServerSource = this.mode,
        advancedToggleButtonVisible: Boolean = this.advancedToggleButtonVisible,
        advancedOptionsVisible: Boolean = this.advancedOptionsVisible,
        formPromptTaggedInput: Boolean = this.formPromptTaggedInput,
        prompt: String = this.prompt,
        negativePrompt: String = this.negativePrompt,
        width: String = this.width,
        height: String = this.height,
        samplingSteps: Int = this.samplingSteps,
        cfgScale: Float = this.cfgScale,
        restoreFaces: Boolean = this.restoreFaces,
        seed: String = this.seed,
        subSeed: String = this.subSeed,
        subSeedStrength: Float = this.subSeedStrength,
        selectedSampler: String = this.selectedSampler,
        availableSamplers: List<String> = this.availableSamplers,
        selectedStylePreset: StabilityAiStylePreset = this.selectedStylePreset,
        selectedClipGuidancePreset: StabilityAiClipGuidance = this.selectedClipGuidancePreset,
        openAiModel: OpenAiModel = this.openAiModel,
        openAiSize: OpenAiSize = this.openAiSize,
        openAiQuality: OpenAiQuality = this.openAiQuality,
        openAiStyle: OpenAiStyle = this.openAiStyle,
        widthValidationError: UiText? = this.widthValidationError,
        heightValidationError: UiText? = this.heightValidationError,
        nsfw: Boolean = this.nsfw,
        batchCount: Int = this.batchCount,
        generateButtonEnabled: Boolean = this.generateButtonEnabled,
    ): GenerationMviState = this
}
