package com.shifthackz.aisdv1.presentation.widget.input.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.unit.dp

@Composable
fun ChipTextFieldItem(
    modifier: Modifier = Modifier,
    text: String,
    showDeleteIcon: Boolean = false,
    onDeleteClick: () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.9f else 0.75f))
            .padding(vertical = 1.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val localColor = MaterialTheme.colorScheme.onPrimary
        Text(
            text = text,
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