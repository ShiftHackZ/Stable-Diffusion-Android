package com.shifthackz.aisdv1.presentation.widget.color

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shifthackz.catppuccin.palette.CatppuccinPalette

@Composable
fun <T : Any> ColorComposable(
    modifier: Modifier = Modifier,
    palette: CatppuccinPalette,
    selectedToken: T,
    token: T,
    color: Color,
    onClick: (Color, T) -> Unit,
    iconTint: Color = palette.Base,
    selectedBorderTint: Color = color.copy(alpha = 0.4f),
    unselectedBorderTint: Color = Color.Transparent,
    borderSize: Dp = 4.dp,
) {
    val selected = token == selectedToken
    Box(
        modifier = modifier
            .background(
                color = if (selected) selectedBorderTint else unselectedBorderTint,
                shape = CircleShape,
            )
            .clip(CircleShape)
            .clickable { onClick(color, token) },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .padding(borderSize)
                .size(1000.dp)
                .background(color = color, shape = CircleShape)
                .clip(CircleShape)
        )
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = iconTint,
            )
        }
    }
}
