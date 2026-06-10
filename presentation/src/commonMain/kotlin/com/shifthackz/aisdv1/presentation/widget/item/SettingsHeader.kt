package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

/**
 * Renders the `SettingsHeader` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param loading loading value consumed by the API.
 * @param text text value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun SettingsHeader(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    text: UiText,
) {
    SettingsHeaderContent(
        modifier = modifier,
        loading = loading,
        text = text.asString(),
    )
}
