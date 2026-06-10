package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

sealed interface TextToImageIntent : MviIntent {
    data object OpenDrawer : TextToImageIntent
    data object NavigateBack : TextToImageIntent
    data object ConfigureProvider : TextToImageIntent
    data object Generate : TextToImageIntent
    data object DismissModal : TextToImageIntent
    data object CancelGeneration : TextToImageIntent
    data object DismissError : TextToImageIntent
    data object DismissMessage : TextToImageIntent
    data object DismissEditTag : TextToImageIntent
    data class SaveResult(val base64: String) : TextToImageIntent
    data class ShareResult(val base64: String) : TextToImageIntent
    data class SaveGenerationResults(val results: List<AiGenerationResult>) : TextToImageIntent
    data class ViewGenerationResult(val result: AiGenerationResult) : TextToImageIntent
    data class ReportGenerationResult(val result: AiGenerationResult) : TextToImageIntent
    data class ShowEditTag(
        val prompt: String,
        val negativePrompt: String,
        val tag: String,
        val isNegative: Boolean,
    ) : TextToImageIntent
    data class ApplyPrompts(
        val prompt: String,
        val negativePrompt: String,
    ) : TextToImageIntent
    data class ApplyGenerationResult(
        val ai: AiGenerationResult,
    ) : TextToImageIntent
    data class UpdateAdvancedOptionsVisibility(val visible: Boolean) : TextToImageIntent
    data class UpdatePrompt(val value: String) : TextToImageIntent
    data class UpdateNegativePrompt(val value: String) : TextToImageIntent
    data class UpdateWidth(val value: String) : TextToImageIntent
    data class UpdateHeight(val value: String) : TextToImageIntent
    data class UpdateSamplingSteps(val value: Int) : TextToImageIntent
    data class UpdateCfgScale(val value: Float) : TextToImageIntent
    data class UpdateRestoreFaces(val value: Boolean) : TextToImageIntent
    data class UpdateSeed(val value: String) : TextToImageIntent
    data class UpdateSubSeed(val value: String) : TextToImageIntent
    data class UpdateSubSeedStrength(val value: Float) : TextToImageIntent
    data class UpdateSampler(val value: String) : TextToImageIntent
    data class UpdateNsfw(val value: Boolean) : TextToImageIntent
    data class UpdateBatchCount(val value: Int) : TextToImageIntent
    data class UpdateOpenAiModel(val value: OpenAiModel) : TextToImageIntent
    data class UpdateOpenAiSize(val value: OpenAiSize) : TextToImageIntent
    data class UpdateOpenAiQuality(val value: OpenAiQuality) : TextToImageIntent
    data class UpdateStabilityAiStyle(val value: StabilityAiStylePreset) : TextToImageIntent
    data class UpdateStabilityAiClipGuidance(val value: StabilityAiClipGuidance) : TextToImageIntent
}
