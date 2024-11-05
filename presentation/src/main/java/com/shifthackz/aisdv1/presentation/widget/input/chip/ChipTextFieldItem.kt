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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.theme.isSdAppInDarkTheme
import com.shifthackz.catppuccin.palette.Catppuccin

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
    val isDark = isSdAppInDarkTheme()
    val bgColor = when (type) {
        ExtraType.Lora -> if (isDark) {
            Catppuccin.Frappe.Lavender
        } else {
            Catppuccin.Latte.Lavender
        }
        ExtraType.HyperNet -> if (isDark) {
            Catppuccin.Frappe.Maroon
        } else {
            Catppuccin.Latte.Maroon
        }
        null -> MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.9f else 0.75f)
    }
    Row(
        modifier = modifier
            .clip(shape)
            .background(bgColor)
            .clickable { onItemClick() }
            .padding(innerPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val localColor = MaterialTheme.colorScheme.onPrimary
        Text(
            text = text,
            overflow = overflow,
            maxLines = maxLines,
            color = localColor,
        )
        if (showDeleteIcon) {
            Icon(
                modifier = Modifier.clickable { onDeleteClick() },
                imageVector = Icons.Default.Close,
                tint = localColor,
                contentDescription = null,
            )
        }
    }
}
