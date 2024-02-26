package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode

abstract class GenerationMviState : MviState {
    abstract val screenModal: Modal
    abstract val mode: GenerationInputMode
    abstract val advancedToggleButtonVisible: Boolean
    abstract val advancedOptionsVisible: Boolean
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
    abstract val widthValidationError: UiText?
    abstract val heightValidationError: UiText?
    abstract val nsfw: Boolean
    abstract val batchCount: Int
    abstract val generateButtonEnabled: Boolean

    open val hasValidationErrors: Boolean
        get() = widthValidationError != null || heightValidationError != null

    open fun copyState(
        screenModal: Modal = this.screenModal,
        mode: GenerationInputMode = this.mode,
        advancedToggleButtonVisible: Boolean = this.advancedToggleButtonVisible,
        advancedOptionsVisible: Boolean = this.advancedOptionsVisible,
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
        widthValidationError: UiText? = this.widthValidationError,
        heightValidationError: UiText? = this.heightValidationError,
        nsfw: Boolean = this.nsfw,
        batchCount: Int = this.batchCount,
        generateButtonEnabled: Boolean = this.generateButtonEnabled,
    ): GenerationMviState = this
}
