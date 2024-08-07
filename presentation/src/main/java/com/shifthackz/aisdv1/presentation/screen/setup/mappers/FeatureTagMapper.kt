package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun FeatureTag.mapToUi(): String {
    return stringResource(
        id = when (this) {
            FeatureTag.Txt2Img -> LocalizationR.string.home_tab_txt_to_img
            FeatureTag.Img2Img -> LocalizationR.string.home_tab_img_to_img
            FeatureTag.OwnServer -> LocalizationR.string.hint_own_server
            FeatureTag.Lora -> LocalizationR.string.title_lora
            FeatureTag.TextualInversion -> LocalizationR.string.title_txt_inversion
            FeatureTag.HyperNetworks -> LocalizationR.string.title_hyper_net
            FeatureTag.Batch -> LocalizationR.string.hint_batch_tag
            FeatureTag.MultipleModels -> LocalizationR.string.hint_multiple_models
            FeatureTag.Offline -> LocalizationR.string.hint_offline_generation
        }
    )
}
