package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviState

abstract class GenerationMviState : MviState {
    abstract val prompt: String
    abstract val negativePrompt: String
    abstract val width: String
    abstract val height: String
    abstract val samplingSteps: Int
    abstract val cfgScale: Float
    abstract val restoreFaces: Boolean
    abstract val seed: String
    abstract val selectedSampler: String
    abstract val availableSamplers: List<String>
    abstract val widthValidationError: UiText?
    abstract val heightValidationError: UiText?

    open val hasValidationErrors: Boolean
        get() = widthValidationError != null || heightValidationError != null

    open fun copyState(
        prompt: String = this.prompt,
        negativePrompt: String = this.negativePrompt,
        width: String = this.width,
        height: String = this.height,
        samplingSteps: Int = this.samplingSteps,
        cfgScale: Float = this.cfgScale,
        restoreFaces: Boolean = this.restoreFaces,
        seed: String = this.seed,
        selectedSampler: String = this.selectedSampler,
        availableSamplers: List<String> = this.availableSamplers,
        widthValidationError: UiText? = this.widthValidationError,
        heightValidationError: UiText? = this.heightValidationError,
    ): GenerationMviState = this
}
