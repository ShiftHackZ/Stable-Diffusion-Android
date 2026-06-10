package com.shifthackz.aisdv1.presentation.widget.input

import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

sealed interface GenerationInputFormEvent {
    data class UpdateAdvancedOptionsVisibility(val visible: Boolean) : GenerationInputFormEvent
    data class UpdatePrompt(val value: String) : GenerationInputFormEvent
    data class UpdateNegativePrompt(val value: String) : GenerationInputFormEvent
    data class EditTag(
        val prompt: String,
        val negativePrompt: String,
        val tag: String,
        val isNegative: Boolean,
    ) : GenerationInputFormEvent

    data class UpdateWidth(val value: String) : GenerationInputFormEvent
    data class UpdateHeight(val value: String) : GenerationInputFormEvent
    data class UpdateSamplingSteps(val value: Int) : GenerationInputFormEvent
    data class UpdateCfgScale(val value: Float) : GenerationInputFormEvent
    data class UpdateRestoreFaces(val value: Boolean) : GenerationInputFormEvent
    data class UpdateSeed(val value: String) : GenerationInputFormEvent
    data class UpdateSubSeed(val value: String) : GenerationInputFormEvent
    data class UpdateSubSeedStrength(val value: Float) : GenerationInputFormEvent
    data class UpdateSampler(val value: String) : GenerationInputFormEvent
    data class UpdateNsfw(val value: Boolean) : GenerationInputFormEvent
    data class UpdateBatch(val value: Int) : GenerationInputFormEvent
    data class UpdateOpenAiModel(val value: OpenAiModel) : GenerationInputFormEvent
    data class UpdateOpenAiSize(val value: OpenAiSize) : GenerationInputFormEvent
    data class UpdateOpenAiQuality(val value: OpenAiQuality) : GenerationInputFormEvent
    data class UpdateStabilityAiStyle(val value: StabilityAiStylePreset) : GenerationInputFormEvent
    data class UpdateStabilityAiClipGuidance(val value: StabilityAiClipGuidance) : GenerationInputFormEvent
}
