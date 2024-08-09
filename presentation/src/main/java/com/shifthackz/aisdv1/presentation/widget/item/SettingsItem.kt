package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import kotlinx.coroutines.delay

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
    val primary = MaterialTheme.colorScheme.primaryContainer
    val secondary = MaterialTheme.colorScheme.tertiaryContainer
    var colorFrom by remember { mutableStateOf(primary) }
    var colorTo by remember { mutableStateOf(secondary) }
    val colorState = remember { Animatable(colorFrom) }
    if (animateBackground) {
        LaunchedEffect(Unit) {
            while (true) {
                colorState.animateTo(colorTo, animationSpec = tween(500))
                delay(500)
                val tmp = colorTo
                colorTo = colorFrom
                colorFrom = tmp
            }
        }
    }

    if (loading) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .defaultMinSize(minHeight = 50.dp)
                .shimmer()
        )
    }
    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    color = if (animateBackground) colorState.value else MaterialTheme.colorScheme.surfaceTint.copy(
                        alpha = 0.8f
                    )
                )
                .defaultMinSize(minHeight = 50.dp)
                .clickable(enabled = enabled) { onClick() }
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SettingsItemContent(
                modifier = endValueContent
                    ?.let { Modifier.fillMaxWidth(0.8f) }
                    ?: Modifier,
                icon = startIcon,
                text = text,
                iconContent = startIconContent,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                endValueContent?.invoke() ?: run {
                    val value = endValueText.asString()
                    if (value.isNotEmpty()) Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = endValueText.asString(),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Right,
                    )
                    if (showChevron) {
                        Icon(
                            modifier = Modifier.padding(horizontal = 6.dp),
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemContent(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    text: UiText,
    iconContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(8.dp))
        icon?.let {
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = it,
                contentDescription = null,
            )
        }
        iconContent?.invoke()
        Text(
            text = text.asString(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
