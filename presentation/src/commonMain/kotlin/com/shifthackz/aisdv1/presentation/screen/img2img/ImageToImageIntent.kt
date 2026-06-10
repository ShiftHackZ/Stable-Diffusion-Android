package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

sealed interface ImageToImageIntent : MviIntent {
    data object OpenDrawer : ImageToImageIntent
    data object NavigateBack : ImageToImageIntent
    data object ConfigureProvider : ImageToImageIntent
    data object NavigateToInPaint : ImageToImageIntent
    data object PickCamera : ImageToImageIntent
    data object PickGallery : ImageToImageIntent
    data object PickRandom : ImageToImageIntent
    data object ClearImageInput : ImageToImageIntent
    data object Generate : ImageToImageIntent
    data object DismissModal : ImageToImageIntent
    data object CancelGeneration : ImageToImageIntent
    data object UndoInPaintStroke : ImageToImageIntent
    data object ClearInPaintMask : ImageToImageIntent
    data object DismissError : ImageToImageIntent
    data object DismissMessage : ImageToImageIntent
    data object DismissEditTag : ImageToImageIntent
    data class DrawInPaintStroke(val stroke: InPaintStroke) : ImageToImageIntent
    data class SaveResult(val base64: String) : ImageToImageIntent
    data class ShareResult(val base64: String) : ImageToImageIntent
    data class SaveGenerationResults(val results: List<AiGenerationResult>) : ImageToImageIntent
    data class ViewGenerationResult(val result: AiGenerationResult) : ImageToImageIntent
    data class ReportGenerationResult(val result: AiGenerationResult) : ImageToImageIntent
    data class ShowEditTag(
        val prompt: String,
        val negativePrompt: String,
        val tag: String,
        val isNegative: Boolean,
    ) : ImageToImageIntent
    data class ApplyPrompts(
        val prompt: String,
        val negativePrompt: String,
    ) : ImageToImageIntent
    data class ApplyGenerationResult(
        val ai: AiGenerationResult,
        val inputImage: Boolean = false,
    ) : ImageToImageIntent
    data class UpdateAdvancedOptionsVisibility(val visible: Boolean) : ImageToImageIntent
    data class UpdatePrompt(val value: String) : ImageToImageIntent
    data class UpdateNegativePrompt(val value: String) : ImageToImageIntent
    data class UpdateWidth(val value: String) : ImageToImageIntent
    data class UpdateHeight(val value: String) : ImageToImageIntent
    data class UpdateSamplingSteps(val value: Int) : ImageToImageIntent
    data class UpdateCfgScale(val value: Float) : ImageToImageIntent
    data class UpdateRestoreFaces(val value: Boolean) : ImageToImageIntent
    data class UpdateSeed(val value: String) : ImageToImageIntent
    data class UpdateSubSeed(val value: String) : ImageToImageIntent
    data class UpdateSubSeedStrength(val value: Float) : ImageToImageIntent
    data class UpdateSampler(val value: String) : ImageToImageIntent
    data class UpdateNsfw(val value: Boolean) : ImageToImageIntent
    data class UpdateBatchCount(val value: Int) : ImageToImageIntent
    data class UpdateOpenAiModel(val value: OpenAiModel) : ImageToImageIntent
    data class UpdateOpenAiSize(val value: OpenAiSize) : ImageToImageIntent
    data class UpdateOpenAiQuality(val value: OpenAiQuality) : ImageToImageIntent
    data class UpdateStabilityAiStyle(val value: StabilityAiStylePreset) : ImageToImageIntent
    data class UpdateStabilityAiClipGuidance(val value: StabilityAiClipGuidance) : ImageToImageIntent
    data class UpdateDenoisingStrength(val value: Float) : ImageToImageIntent
    data class UpdateInPaintBrushSize(val value: Int) : ImageToImageIntent
    data class UpdateInPaintMaskBlur(val value: Int) : ImageToImageIntent
    data class UpdateInPaintOnlyMaskedPadding(val value: Int) : ImageToImageIntent
    data class UpdateInPaintMaskMode(val value: ImageInPaintState.MaskMode) : ImageToImageIntent
    data class UpdateInPaintMaskContent(val value: ImageInPaintState.MaskContent) : ImageToImageIntent
    data class UpdateInPaintArea(val value: ImageInPaintState.Area) : ImageToImageIntent
}
