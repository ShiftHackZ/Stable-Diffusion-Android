package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    selected: Boolean = false,
    startIcon: ImageVector? = null,
    text: UiText,
    animateBackground: Boolean = false,
    showChevron: Boolean = true,
    endValueText: UiText = UiText.empty,
    endValueContent: (@Composable () -> Unit)? = null,
    startIconContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    SettingsItem(
        modifier = modifier,
        loading = loading,
        enabled = enabled,
        selected = selected,
        text = text.asString(),
        animateBackground = animateBackground,
        showChevron = showChevron,
        endValueText = endValueText.asString(),
        endValueContent = endValueContent,
        startIconContent = settingsStartIconContent(
            startIcon = startIcon,
            customContent = startIconContent,
        ),
        onClick = onClick,
    )
}

@Composable
fun SettingsItemContent(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: UiText,
    iconContent: (@Composable () -> Unit)? = null,
) {
    SettingsItemContent(
        modifier = modifier,
        text = text.asString(),
        startIconContent = settingsStartIconContent(
            startIcon = icon,
            customContent = iconContent,
        ),
    )
}

private fun settingsStartIconContent(
    startIcon: ImageVector?,
    customContent: (@Composable () -> Unit)?,
): (@Composable () -> Unit)? {
    if (startIcon == null && customContent == null) return null
    return {
        startIcon?.let {
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = it,
                contentDescription = null,
            )
        }
        customContent?.invoke()
    }
}
