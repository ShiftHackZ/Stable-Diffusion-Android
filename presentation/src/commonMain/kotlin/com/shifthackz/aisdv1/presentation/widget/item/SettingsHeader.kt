package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

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
