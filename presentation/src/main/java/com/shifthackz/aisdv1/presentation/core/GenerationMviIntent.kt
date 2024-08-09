package com.shifthackz.aisdv1.presentation.core

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.OpenAiModel
import com.shifthackz.aisdv1.domain.entity.OpenAiQuality
import com.shifthackz.aisdv1.domain.entity.OpenAiSize
import com.shifthackz.aisdv1.domain.entity.OpenAiStyle
import com.shifthackz.aisdv1.domain.entity.StabilityAiClipGuidance
import com.shifthackz.aisdv1.domain.entity.StabilityAiStylePreset
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.android.core.mvi.MviIntent
import com.shz.imagepicker.imagepicker.model.PickedResult

sealed interface GenerationMviIntent : MviIntent {

    data class NewPrompts(
        val positive: String,
        val negative: String,
    ) : GenerationMviIntent

    data class SetAdvancedOptionsVisibility(val visible: Boolean) : GenerationMviIntent

    sealed interface Update : GenerationMviIntent {

        data class Prompt(val value: String) : Update

        data class NegativePrompt(val value: String) : Update

        sealed interface Size : Update {

            data class Width(val value: String) : Size

            data class Height(val value: String) : Size
        }

        data class SamplingSteps(val value: Int) : Update

        data class CfgScale(val value: Float) : Update

        data class RestoreFaces(val value: Boolean) : Update

        data class Seed(val value: String) : Update

        data class SubSeed(val value: String) : Update

        data class SubSeedStrength(val value: Float) : Update

        data class Sampler(val value: String) : Update

        data class Nsfw(val value: Boolean) : Update

        data class Batch(val value: Int) : Update

        sealed interface OpenAi : Update {

            data class Model(val value: OpenAiModel) : OpenAi

            data class Size(val value: OpenAiSize) : OpenAi

            data class Quality(val value: OpenAiQuality) : OpenAi

            data class Style(val value: OpenAiStyle) : OpenAi
        }

        sealed interface StabilityAi : Update {
            data class Style(val value: StabilityAiStylePreset) : StabilityAi

            data class ClipGuidance(val value: StabilityAiClipGuidance) : StabilityAi
        }
    }

    sealed interface Result : GenerationMviIntent {

        data class Save(val ai: List<AiGenerationResult>) : Result

        data class View(val ai: AiGenerationResult) : Result
    }

    data class SetModal(val modal: Modal) : GenerationMviIntent

    enum class Cancel : GenerationMviIntent {
        Generation, FetchRandomImage,
    }

    data object Configuration : GenerationMviIntent

    data object Generate : GenerationMviIntent

    data class UpdateFromGeneration(
        val payload: GenerationFormUpdateEvent.Payload,
    ) : GenerationMviIntent

    data class Drawer(val intent: DrawerIntent) : GenerationMviIntent
}

sealed interface ImageToImageIntent : GenerationMviIntent {

    data object InPaint : ImageToImageIntent

    data object FetchRandomPhoto : ImageToImageIntent

    data object ClearImageInput : ImageToImageIntent

    data class UpdateDenoisingStrength(val value: Float) : ImageToImageIntent

    data class UpdateImage(val bitmap: Bitmap) : ImageToImageIntent

    data class CropImage(val result: PickedResult) : ImageToImageIntent

    enum class Pick : ImageToImageIntent {
        Camera, Gallery
    }
}
