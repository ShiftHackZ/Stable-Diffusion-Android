package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import kotlinx.coroutines.delay

@Composable
fun SettingsItem(
    modifier: Modifier = Modifier,
    startIcon: ImageVector,
    text: UiText,
    animateBackground: Boolean = false,
    endValueText: UiText = UiText.empty,
    endValueContent: (@Composable () -> Unit)? = null,
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
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = if (animateBackground) colorState.value else MaterialTheme.colorScheme.primaryContainer)
            .defaultMinSize(minHeight = 50.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = endValueContent
                ?.let { Modifier.fillMaxWidth(0.8f) }
                ?: Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = startIcon,
                contentDescription = null,
            )
            Text(
                text = text.asString(),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
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
                Icon(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                )
            }
        }
    }
}
