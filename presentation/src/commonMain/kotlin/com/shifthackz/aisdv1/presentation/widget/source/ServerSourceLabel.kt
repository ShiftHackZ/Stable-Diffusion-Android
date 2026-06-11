package com.shifthackz.aisdv1.presentation.widget.source

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Renders the `getName` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
fun ServerSource.getName(): String = getNameUiText().asString()

/**
 * Loads SDAI data through `getNameUiText`.
 *
 * @author Dmitriy Moroz
 */
fun ServerSource.getNameUiText(): UiText = Localization.string(
    when (this) {
        ServerSource.AUTOMATIC1111 -> "srv_type_own"
        ServerSource.HORDE -> "srv_type_horde"
        ServerSource.LOCAL_MICROSOFT_ONNX -> "srv_type_local"
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> "srv_type_media_pipe"
        ServerSource.LOCAL_APPLE_CORE_ML -> return UiText.Static("Silicon Diffusion Core ML")
        ServerSource.HUGGING_FACE -> "srv_type_hugging_face"
        ServerSource.OPEN_AI -> "srv_type_open_ai"
        ServerSource.STABILITY_AI -> "srv_type_stability_ai"
        ServerSource.SWARM_UI -> "srv_type_swarm_ui"
    },
).asUiText()
