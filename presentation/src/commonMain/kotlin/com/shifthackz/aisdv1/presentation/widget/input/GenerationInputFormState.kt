package com.shifthackz.aisdv1.presentation.widget.input

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.domain.entity.ADetailerConfig
import com.shifthackz.aisdv1.domain.entity.FalAiAcceleration
import com.shifthackz.aisdv1.domain.entity.FalAiImageSize
import com.shifthackz.aisdv1.domain.entity.FalAiModel
import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.HiresConfig
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.Scheduler
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset

/**
 * Defines the `GenerationInputFormState` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface GenerationInputFormState {
    /**
     * Exposes the `onBoardingDemo` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val onBoardingDemo: Boolean
    /**
     * Exposes the `mode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mode: ServerSource
    /**
     * Exposes the `advancedToggleButtonVisible` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val advancedToggleButtonVisible: Boolean
    /**
     * Exposes the `advancedOptionsVisible` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val advancedOptionsVisible: Boolean
    /**
     * Exposes the `formPromptTaggedInput` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val formPromptTaggedInput: Boolean
    /**
     * Exposes the `prompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String
    /**
     * Exposes the `negativePrompt` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String
    /**
     * Exposes the `width` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val width: String
    /**
     * Exposes the `height` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val height: String
    /**
     * Exposes the `samplingSteps` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val samplingSteps: Int
    /**
     * Exposes the `cfgScale` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val cfgScale: Float
    /**
     * Exposes the `restoreFaces` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val restoreFaces: Boolean
    /**
     * Exposes the `seed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val seed: String
    /**
     * Exposes the `subSeed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeed: String
    /**
     * Exposes the `subSeedStrength` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val subSeedStrength: Float
    /**
     * Exposes the `selectedSampler` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedSampler: String
    /**
     * Exposes the `selectedScheduler` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedScheduler: Scheduler
    /**
     * Exposes the `availableForgeModules` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val availableForgeModules: List<ForgeModule>
    /**
     * Exposes the `selectedForgeModules` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedForgeModules: List<ForgeModule>
    /**
     * Exposes the `availableSamplers` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val availableSamplers: List<String>
    /**
     * Exposes the `selectedStylePreset` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedStylePreset: StabilityAiStylePreset
    /**
     * Exposes the `selectedClipGuidancePreset` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectedClipGuidancePreset: StabilityAiClipGuidance
    /**
     * Exposes the `openAiModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiModel: OpenAiModel
    /**
     * Exposes the `openAiSize` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiSize: OpenAiSize
    /**
     * Exposes the `openAiQuality` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiQuality: OpenAiQuality
    /**
     * Exposes the `falAiModel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiModel: FalAiModel
    /**
     * Exposes the `falAiImageSize` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiImageSize: FalAiImageSize
    /**
     * Exposes the `falAiAcceleration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiAcceleration: FalAiAcceleration
    /**
     * Exposes the `falAiSyncMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiSyncMode: Boolean
    val sdxlBackend: SdxlBackend
    val arliAiModels: List<String>
    val arliAiModel: String
    /**
     * Exposes the `widthValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val widthValidationError: UiText?
    /**
     * Exposes the `heightValidationError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val heightValidationError: UiText?
    /**
     * Exposes the `nsfw` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val nsfw: Boolean
    /**
     * Exposes the `batchCount` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val batchCount: Int
    /**
     * Exposes the `hires` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hires: HiresConfig
    /**
     * Exposes the `aDetailer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val aDetailer: ADetailerConfig
    /**
     * Exposes the `aDetailerAvailable` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val aDetailerAvailable: Boolean
    /**
     * Exposes the `aDetailerRefreshing` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val aDetailerRefreshing: Boolean

    /**
     * Exposes the `promptKeywords` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val promptKeywords: List<String>
        get() = prompt.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

    /**
     * Exposes the `negativePromptKeywords` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePromptKeywords: List<String>
        get() = negativePrompt.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
}
