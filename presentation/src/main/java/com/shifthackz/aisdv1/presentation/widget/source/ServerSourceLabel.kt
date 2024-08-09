package com.shifthackz.aisdv1.presentation.widget.source

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ServerSource.getName(): String {
    return getNameUiText().asString()
}

fun ServerSource.getNameUiText(): UiText = when (this) {
    ServerSource.AUTOMATIC1111 -> LocalizationR.string.srv_type_own
    ServerSource.HORDE -> LocalizationR.string.srv_type_horde
    ServerSource.LOCAL -> LocalizationR.string.srv_type_local
    ServerSource.HUGGING_FACE -> LocalizationR.string.srv_type_hugging_face
    ServerSource.OPEN_AI -> LocalizationR.string.srv_type_open_ai
    ServerSource.STABILITY_AI -> LocalizationR.string.srv_type_stability_ai
    ServerSource.SWARM_UI -> LocalizationR.string.srv_type_swarm_ui
}.asUiText()
