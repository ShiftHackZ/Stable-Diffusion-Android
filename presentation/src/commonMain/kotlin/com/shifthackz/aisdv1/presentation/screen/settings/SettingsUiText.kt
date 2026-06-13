@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Executes the `text` step in the SDAI presentation layer.
 *
 * @param key key value consumed by the API.
 * @author Dmitriy Moroz
 */
internal fun text(key: String): UiText = Localization.string(key).asUiText()

/**
 * Executes the `shortTitle` step in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal fun ServerSource.shortTitle(): String = when (this) {
    ServerSource.AUTOMATIC1111 -> Localization.string("srv_type_own_short")
    ServerSource.HORDE -> Localization.string("srv_type_horde_short")
    ServerSource.HUGGING_FACE -> Localization.string("srv_type_hugging_face_short")
    ServerSource.OPEN_AI -> Localization.string("srv_type_open_ai")
    ServerSource.STABILITY_AI -> Localization.string("srv_type_stability_ai")
    ServerSource.FAL_AI -> Localization.string("srv_type_fal_ai")
    ServerSource.LOCAL_MICROSOFT_ONNX -> Localization.string("srv_type_local_short")
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Localization.string("srv_type_media_pipe_short")
    ServerSource.LOCAL_STABLE_DIFFUSION_CPP -> Localization.string("srv_type_sdxl_short")
    ServerSource.LOCAL_APPLE_CORE_ML -> "Core ML"
    ServerSource.SWARM_UI -> Localization.string("srv_type_swarm_ui")
}
