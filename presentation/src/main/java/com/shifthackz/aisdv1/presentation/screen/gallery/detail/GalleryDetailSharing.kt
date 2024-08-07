package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.content.Context
import com.shifthackz.aisdv1.core.sharing.shareText
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class GalleryDetailSharing {

    operator fun invoke(context: Context, state: GalleryDetailState) {
        val data = when (state) {
            is GalleryDetailState.Content -> buildString {
                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_type)}: " +
                            state.type.asString(context)
                )

                val prompt = state.prompt.asString(context)
                if (prompt.isNotEmpty()) {
                    appendLine(
                        "${context.getString(LocalizationR.string.gallery_info_field_prompt)}: " +
                                prompt
                    )
                }

                val negativePrompt = state.negativePrompt.asString(context)
                if (negativePrompt.isNotEmpty()) {
                    appendLine(
                        "${context.getString(LocalizationR.string.gallery_info_field_negative_prompt)}: " +
                                negativePrompt
                    )
                }

                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_size)}: " +
                            state.size.asString(context)
                )
                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_sampler)}: " +
                            state.sampler.asString(context)
                )
                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_sampling_steps)}: " +
                            state.samplingSteps.asString(context)
                )
                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_cfg)}: " +
                            state.cfgScale.asString(context)
                )
                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_restore_faces)}: " +
                            state.restoreFaces.asString(context)
                )
                appendLine(
                    "${context.getString(LocalizationR.string.gallery_info_field_seed)}: " +
                            state.seed.asString(context)
                )
            }
            else -> ""
        }
        data.takeIf(String::isNotEmpty)?.let(context::shareText)
    }
}
