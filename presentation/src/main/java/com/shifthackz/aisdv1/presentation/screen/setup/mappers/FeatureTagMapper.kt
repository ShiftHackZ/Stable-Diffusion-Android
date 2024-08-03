package com.shifthackz.aisdv1.presentation.screen.setup.mappers

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.presentation.R

@Composable
fun FeatureTag.mapToUi(): String {
    return stringResource(
        id = when (this) {
            FeatureTag.Txt2Img -> R.string.home_tab_txt_to_img
            FeatureTag.Img2Img -> R.string.home_tab_img_to_img
            FeatureTag.OwnServer -> R.string.hint_own_server
            FeatureTag.Lora -> R.string.title_lora
            FeatureTag.TextualInversion -> R.string.title_txt_inversion
            FeatureTag.HyperNetworks -> R.string.title_hyper_net
            FeatureTag.Batch -> R.string.hint_batch_tag
            FeatureTag.MultipleModels -> R.string.hint_multiple_models
            FeatureTag.Offline -> R.string.hint_offline_generation
        }
    )
}
