package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.FeatureTag

/**
 * Maps provider capability tags to localized chip labels.
 */
fun FeatureTag.mapToUi(): String = Localization.string(
    when (this) {
        FeatureTag.Txt2Img -> "provider_tag_text_to_image"
        FeatureTag.Img2Img -> "provider_tag_image_to_image"
        FeatureTag.OwnServer -> "provider_tag_own_server"
        FeatureTag.Lora -> "provider_tag_lora"
        FeatureTag.TextualInversion -> "provider_tag_textual_inversion"
        FeatureTag.HyperNetworks -> "provider_tag_hypernetworks"
        FeatureTag.Batch -> "provider_tag_batch"
        FeatureTag.MultipleModels -> "provider_tag_multiple_models"
        FeatureTag.Offline -> "provider_tag_offline"
    },
)
