package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap

@Immutable
data class GalleryDetailState(
    val loading: Boolean = true,
    val tabs: List<GalleryDetailTab> = emptyList(),
    val selectedTab: GalleryDetailTab = GalleryDetailTab.IMAGE,
    val dialog: GalleryDetailDialog = GalleryDetailDialog.None,
    val content: GalleryDetailContent? = null,
) : MviState

@Immutable
data class GalleryDetailContent(
    val showReportButton: Boolean = false,
    val generationType: AiGenerationResult.Type,
    val id: Long,
    val imageBase64: String,
    val image: ImageBitmap?,
    val inputImageBase64: String?,
    val inputImage: ImageBitmap?,
    val createdAt: String,
    val type: String,
    val prompt: String,
    val negativePrompt: String,
    val size: String,
    val samplingSteps: String,
    val cfgScale: String,
    val restoreFaces: String,
    val sampler: String,
    val seed: String,
    val subSeed: String,
    val subSeedStrength: String,
    val denoisingStrength: String,
    val hidden: Boolean,
)

@Immutable
sealed interface GalleryDetailDialog {
    data object None : GalleryDetailDialog
    data object DeleteConfirm : GalleryDetailDialog
    data class Error(val message: String) : GalleryDetailDialog
}

enum class GalleryDetailTab {
    IMAGE,
    ORIGINAL,
    INFO;

    companion object {
        fun consume(type: AiGenerationResult.Type): List<GalleryDetailTab> = when (type) {
            AiGenerationResult.Type.TEXT_TO_IMAGE -> listOf(IMAGE, INFO)
            AiGenerationResult.Type.IMAGE_TO_IMAGE -> entries
        }
    }
}

internal fun AiGenerationResult.toGalleryDetailContent(
    showReportButton: Boolean,
): GalleryDetailContent =
    GalleryDetailContent(
        showReportButton = showReportButton,
        generationType = type,
        id = id,
        imageBase64 = image,
        image = image.decodeBase64ImageBitmap(),
        inputImageBase64 = inputImage.takeIf { type == AiGenerationResult.Type.IMAGE_TO_IMAGE },
        inputImage = inputImage
            .takeIf { type == AiGenerationResult.Type.IMAGE_TO_IMAGE }
            ?.decodeBase64ImageBitmap(),
        createdAt = formatGalleryCreatedAt(createdAt),
        type = type.key,
        prompt = prompt,
        negativePrompt = negativePrompt,
        size = "$width X $height",
        samplingSteps = samplingSteps.toString(),
        cfgScale = cfgScale.toString(),
        restoreFaces = Localization.string(if (restoreFaces) "yes" else "no"),
        sampler = sampler,
        seed = seed,
        subSeed = subSeed,
        subSeedStrength = subSeedStrength.toString(),
        denoisingStrength = denoisingStrength.toString(),
        hidden = hidden,
    )

internal fun GalleryDetailContent.paramsText(): String = buildString {
    appendLine("${Localization.string("gallery_info_field_type")}: $type")

    if (prompt.isNotEmpty()) {
        appendLine("${Localization.string("gallery_info_field_prompt")}: $prompt")
    }

    if (negativePrompt.isNotEmpty()) {
        appendLine("${Localization.string("gallery_info_field_negative_prompt")}: $negativePrompt")
    }

    appendLine("${Localization.string("gallery_info_field_size")}: $size")
    appendLine("${Localization.string("gallery_info_field_sampler")}: $sampler")
    appendLine("${Localization.string("gallery_info_field_sampling_steps")}: $samplingSteps")
    appendLine("${Localization.string("gallery_info_field_cfg")}: $cfgScale")
    appendLine("${Localization.string("gallery_info_field_restore_faces")}: $restoreFaces")
    appendLine("${Localization.string("gallery_info_field_seed")}: $seed")
}.trim()

internal fun GalleryDetailContent.selectedImageBase64(selectedTab: GalleryDetailTab): String =
    if (
        generationType == AiGenerationResult.Type.IMAGE_TO_IMAGE &&
        inputImageBase64 != null &&
        selectedTab == GalleryDetailTab.ORIGINAL
    ) {
        inputImageBase64
    } else {
        imageBase64
    }
