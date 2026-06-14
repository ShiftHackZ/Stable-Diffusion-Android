package com.shifthackz.aisdv1.presentation.widget.image

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Defines image decode state for Base64-backed UI images.
 *
 * @author Dmitriy Moroz
 */
@Immutable
internal sealed interface DecodedImageBitmap {
    /**
     * Provides the `Loading` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Loading : DecodedImageBitmap
    /**
     * Carries `Ready` data through the SDAI presentation layer.
     *
     * @param image image value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Ready(val image: ImageBitmap) : DecodedImageBitmap
    /**
     * Provides the `Unavailable` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unavailable : DecodedImageBitmap
}

/**
 * Decodes Base64 image content away from the UI thread.
 *
 * @param key stable image key value consumed by the API.
 * @param base64 Base64 image value consumed by the API.
 * @return Result produced by `rememberDecodedImageBitmap`.
 * @author Dmitriy Moroz
 */
@Composable
internal fun rememberDecodedImageBitmap(
    key: Any?,
    base64: String?,
): State<DecodedImageBitmap> =
    produceState<DecodedImageBitmap>(
        initialValue = DecodedImageBitmap.Loading,
        key1 = key,
        key2 = base64,
    ) {
        val source = base64?.takeIf(String::isNotBlank)
        if (source == null) {
            value = DecodedImageBitmap.Unavailable
            return@produceState
        }
        value = DecodedImageBitmap.Loading
        value = withContext(Dispatchers.Default) {
            source.decodeBase64ImageBitmap()
        }?.let(DecodedImageBitmap::Ready) ?: DecodedImageBitmap.Unavailable
    }
