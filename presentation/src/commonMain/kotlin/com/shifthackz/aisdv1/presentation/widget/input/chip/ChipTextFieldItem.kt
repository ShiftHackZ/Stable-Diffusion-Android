package com.shifthackz.aisdv1.presentation.widget.input.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.model.ExtraType

/**
 * Renders the `ChipTextFieldItem` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param type type value consumed by the API.
 * @param text text value consumed by the API.
 * @param overflow overflow value consumed by the API.
 * @param shape shape value consumed by the API.
 * @param innerPadding inner padding value consumed by the API.
 * @param maxLines max lines value consumed by the API.
 * @param showDeleteIcon show delete icon value consumed by the API.
 * @param onDeleteClick callback invoked by the component.
 * @param onItemClick callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
fun ChipTextFieldItem(
    modifier: Modifier = Modifier,
    type: ExtraType? = null,
    text: String,
    overflow: TextOverflow = TextOverflow.Clip,
    shape: Shape = RoundedCornerShape(4.dp),
    innerPadding: PaddingValues = PaddingValues(vertical = 1.dp, horizontal = 2.dp),
    maxLines: Int = Int.MAX_VALUE,
    showDeleteIcon: Boolean = false,
    onDeleteClick: () -> Unit = {},
    onItemClick: () -> Unit = {},
) {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val (bgColor, contentColor) = when (type) {
        ExtraType.Lora -> MaterialTheme.colorScheme.tertiaryContainer to
                MaterialTheme.colorScheme.onTertiaryContainer
        ExtraType.HyperNet -> MaterialTheme.colorScheme.secondaryContainer to
                MaterialTheme.colorScheme.onSecondaryContainer
        null -> MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.9f else 0.75f) to
                MaterialTheme.colorScheme.onPrimary
    }
    Row(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .clickable { onItemClick() }
            .padding(innerPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            overflow = overflow,
            maxLines = maxLines,
            color = contentColor,
        )
        if (showDeleteIcon) {
            Icon(
                modifier = Modifier.clickable { onDeleteClick() },
                imageVector = Icons.Default.Close,
                tint = contentColor,
                contentDescription = null,
            )
        }
    }
}
