package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

@Composable
fun SettingsHeader(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    text: UiText,
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            @Composable fun line(inverse: Boolean = false) {
                val brush = Brush.horizontalGradient(
                    buildList {
                        if (inverse) {
                            add(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                            add(LocalContentColor.current.copy(alpha = 0.2f))
                            add(Color.Transparent)
                        } else {
                            add(Color.Transparent)
                            add(LocalContentColor.current.copy(alpha = 0.2f))
                            add(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                        }
                    }
                )
                Spacer(
                    modifier = Modifier
                        .height(1.5.dp)
                        .background(brush, RoundedCornerShape(2.dp))
                        .weight(1f)
                        .then(if (loading) Modifier.shimmer() else Modifier),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            line()
            AnimatedContent(
                targetState = !loading,
                label = "settings_header",
                transitionSpec = { fadeIn() togetherWith fadeOut() },
            ) { contentVisible ->
                if (contentVisible) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .alpha(if (loading) 0f else 1f),
                        text = text.asString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = LocalContentColor.current,
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.33f)
                            .padding(horizontal = 12.dp)
                            .defaultMinSize(minHeight = 24.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmer(),
                    )
                }
            }
            line(true)
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
@Preview(name = "Settings Header loading UI state")
private fun LoadingPreview() {
    SettingsHeader(loading = true, text = UiText.empty)
}

@Composable
@Preview(name = "Settings Header content UI state")
private fun ContentPreview() {
    SettingsHeader(
        loading = false,
        text = UiText.Static("Header text"),
    )
}
