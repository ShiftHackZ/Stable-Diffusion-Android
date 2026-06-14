package com.shifthackz.aisdv1.presentation.widget.source

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization

/**
 * Renders the compact beta badge used by provider readiness metadata.
 *
 * @param modifier Compose modifier applied to the badge.
 * @author Dmitriy Moroz
 */
@Composable
fun BetaBadge(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .padding(start = 6.dp)
            .background(
                color = Color(0xFFFFD54F),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = Localization.string("provider_readiness_beta"),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.W500,
        color = Color(0xFF3B2F00),
    )
}
