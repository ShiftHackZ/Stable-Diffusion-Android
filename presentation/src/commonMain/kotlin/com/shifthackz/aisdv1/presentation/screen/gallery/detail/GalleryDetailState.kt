package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap

/**
 * Carries `GalleryDetailState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class GalleryDetailState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `tabs` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val tabs: List<GalleryDetailTab> = emptyList(),
    /**
     * Exposes the `selectedTab` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedTab: GalleryDetailTab = GalleryDetailTab.IMAGE,
    /**
     * Exposes the `dialog` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val dialog: GalleryDetailDialog = GalleryDetailDialog.None,
    /**
     * Exposes the `content` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val content: GalleryDetailContent? = null,
) : MviState

/**
 * Carries `GalleryDetailContent` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class GalleryDetailContent(
    /**
     * Exposes the `showReportButton` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showReportButton: Boolean = false,
    /**
     * Exposes the `generationType` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val generationType: AiGenerationResult.Type,
    /**
     * Exposes the `id` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val id: Long,
    /**
     * Exposes the `imageBase64` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val imageBase64: String,
    /**
     * Exposes the `image` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val image: ImageBitmap?,
    /**
     * Exposes the `inputImageBase64` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val inputImageBase64: String?,
    /**
     * Exposes the `inputImage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val inputImage: ImageBitmap?,
    /**
     * Exposes the `createdAt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val createdAt: String,
    /**
     * Exposes the `type` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val type: String,
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String,
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String,
    /**
     * Exposes the `size` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val size: String,
    /**
     * Exposes the `samplingSteps` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val samplingSteps: String,
    /**
     * Exposes the `cfgScale` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val cfgScale: String,
    /**
     * Exposes the `restoreFaces` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val restoreFaces: String,
    /**
     * Exposes the `sampler` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sampler: String,
    /**
     * Exposes the `seed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val seed: String,
    /**
     * Exposes the `subSeed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeed: String,
    /**
     * Exposes the `subSeedStrength` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeedStrength: String,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: String,
    /**
     * Exposes the `hidden` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hidden: Boolean,
)

/**
 * Defines the `GalleryDetailDialog` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
sealed interface GalleryDetailDialog {
    /**
     * Provides the `None` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : GalleryDetailDialog
    /**
     * Provides the `DeleteConfirm` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DeleteConfirm : GalleryDetailDialog
    /**
     * Carries `Error` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Error(val message: String) : GalleryDetailDialog
}

/**
 * Coordinates `GalleryDetailTab` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
enum class GalleryDetailTab {
    IMAGE,
    ORIGINAL,
    INFO;

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `consume` step in the SDAI presentation layer.
         *
         * @param type type value consumed by the API.
         * @author Dmitriy Moroz
         */
        fun consume(type: AiGenerationResult.Type): List<GalleryDetailTab> = when (type) {
            AiGenerationResult.Type.TEXT_TO_IMAGE -> listOf(IMAGE, INFO)
            AiGenerationResult.Type.IMAGE_TO_IMAGE -> entries
        }
    }
}

/**
 * Renders the `toGalleryDetailContent` UI for the SDAI presentation layer.
 *
 * @param showReportButton show report button value consumed by the API.
 * @return Result produced by `toGalleryDetailContent`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `paramsText` step in the SDAI presentation layer.
 *
 * @return Result produced by `paramsText`.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `selectedImageBase64` step in the SDAI presentation layer.
 *
 * @param selectedTab selected tab value consumed by the API.
 * @return Result produced by `selectedImageBase64`.
 * @author Dmitriy Moroz
 */
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
