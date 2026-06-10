package com.shifthackz.aisdv1.presentation.widget.input

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

interface GenerationInputFormState {
    val onBoardingDemo: Boolean
    val mode: ServerSource
    val advancedToggleButtonVisible: Boolean
    val advancedOptionsVisible: Boolean
    val formPromptTaggedInput: Boolean
    val prompt: String
    val negativePrompt: String
    val width: String
    val height: String
    val samplingSteps: Int
    val cfgScale: Float
    val restoreFaces: Boolean
    val seed: String
    val subSeed: String
    val subSeedStrength: Float
    val selectedSampler: String
    val availableSamplers: List<String>
    val selectedStylePreset: StabilityAiStylePreset
    val selectedClipGuidancePreset: StabilityAiClipGuidance
    val openAiModel: OpenAiModel
    val openAiSize: OpenAiSize
    val openAiQuality: OpenAiQuality
    val widthValidationError: UiText?
    val heightValidationError: UiText?
    val nsfw: Boolean
    val batchCount: Int

    val promptKeywords: List<String>
        get() = prompt.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    val negativePromptKeywords: List<String>
        get() = negativePrompt.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
