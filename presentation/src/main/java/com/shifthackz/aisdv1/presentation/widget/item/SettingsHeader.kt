package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
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
    if (loading) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.33f)
                .defaultMinSize(minHeight = 24.dp)
                .clip(RoundedCornerShape(10.dp))
                .shimmer(),
        )
    }
    AnimatedVisibility(
        visible = !loading,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Text(
            modifier = modifier.alpha(if (loading) 0f else 1f),
            text = text.asString(),
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}
