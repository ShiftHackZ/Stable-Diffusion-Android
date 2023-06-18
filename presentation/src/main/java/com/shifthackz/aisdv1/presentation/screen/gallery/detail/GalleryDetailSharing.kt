package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.content.Context
import com.shifthackz.aisdv1.core.sharing.shareText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R

class GalleryDetailSharing {

    operator fun invoke(context: Context, ai: AiGenerationResult) {
        val data = buildString {
            appendLine(
                "${context.getString(R.string.gallery_info_field_type)}: " +
                        ai.type.key
            )

            if (ai.prompt.isNotEmpty()) {
                appendLine(
                    "${context.getString(R.string.gallery_info_field_prompt)}: " +
                            ai.prompt
                )
            }

            if (ai.negativePrompt.isNotEmpty()) {
                appendLine(
                    "${context.getString(R.string.gallery_info_field_negative_prompt)}: " +
                            ai.negativePrompt
                )
            }

            appendLine(
                "${context.getString(R.string.gallery_info_field_size)}: " +
                       "${ai.width} X ${ai.height}"
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_sampler)}: " +
                        ai.sampler
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_sampling_steps)}: " +
                        "${ai.samplingSteps}"
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_cfg)}: " +
                        "${ai.cfgScale}"
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_restore_faces)}: " +
                        "${ai.restoreFaces}"
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_seed)}: " +
                        ai.seed
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_sub_seed)}: " +
                        ai.subSeed
            )
            appendLine(
                "${context.getString(R.string.gallery_info_field_sub_seed_strength)}: " +
                        "${ai.subSeedStrength}"
            )
            if (ai.type == AiGenerationResult.Type.IMAGE_TO_IMAGE) {
                appendLine(
                    "${context.getString(R.string.gallery_info_field_denoising_strength)}: " +
                            "${ai.denoisingStrength}"
                )
            }
        }
        data.takeIf(String::isNotEmpty)?.let(context::shareText)
    }
}
