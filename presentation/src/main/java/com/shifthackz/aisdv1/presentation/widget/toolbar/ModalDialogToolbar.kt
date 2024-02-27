package com.shifthackz.aisdv1.presentation.widget.toolbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.shimmer

@Composable
fun ModalDialogToolbar(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    text: String,
    onClose: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(40.dp))
        Spacer(modifier = Modifier.weight(1f))
        if (!loading) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.28f)
                    .height(26.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmer()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
            )
        }
    }
}