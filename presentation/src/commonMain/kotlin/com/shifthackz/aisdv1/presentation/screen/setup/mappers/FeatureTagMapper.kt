package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.FeatureTag

/**
 * Converts SDAI data with `mapToUi`.
 *
 * @author Dmitriy Moroz
 */
fun FeatureTag.mapToUi(): String = Localization.string(
    when (this) {
        FeatureTag.Txt2Img -> "home_tab_txt_to_img"
        FeatureTag.Img2Img -> "home_tab_img_to_img"
        FeatureTag.OwnServer -> "hint_own_server"
        FeatureTag.Lora -> "title_lora"
        FeatureTag.TextualInversion -> "title_txt_inversion"
        FeatureTag.HyperNetworks -> "title_hyper_net"
        FeatureTag.Batch -> "hint_batch_tag"
        FeatureTag.MultipleModels -> "hint_multiple_models"
        FeatureTag.Offline -> "hint_offline_generation"
    },
)
