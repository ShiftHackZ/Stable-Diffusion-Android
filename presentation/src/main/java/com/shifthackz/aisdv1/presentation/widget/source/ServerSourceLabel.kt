package com.shifthackz.aisdv1.presentation.widget.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R

@Composable
fun ServerSource.getName(): String {
    return stringResource(id = when (this) {
        ServerSource.AUTOMATIC1111 -> R.string.srv_type_own
        ServerSource.HORDE -> R.string.srv_type_horde
        ServerSource.LOCAL -> R.string.srv_type_local
        ServerSource.HUGGING_FACE -> R.string.srv_type_hugging_face
        ServerSource.OPEN_AI -> R.string.srv_type_open_ai
        ServerSource.STABILITY_AI -> R.string.srv_type_stability_ai
        ServerSource.SWARM_UI -> R.string.srv_type_swarm_ui
    })
}
