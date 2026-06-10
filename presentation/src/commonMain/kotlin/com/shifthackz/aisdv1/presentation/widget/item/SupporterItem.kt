package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.domain.entity.Supporter

/**
 * Renders the `SupporterItem` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param supporter supporter value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun SupporterItem(
    modifier: Modifier = Modifier,
    supporter: Supporter? = null,
) {
    Column(
        modifier = modifier.padding(12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val iconShape = CircleShape
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(MaterialTheme.colorScheme.onPrimary, iconShape)
                    .clip(iconShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = supporter?.name?.firstOrNull()?.uppercaseChar()?.toString().orEmpty(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                )
            }
            Text(
                text = supporter?.name ?: "",
                fontWeight = FontWeight.W500,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = supporter?.date?.formatSupporterDate() ?: "",
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
            )
        }
        supporter?.message?.takeIf(String::isNotBlank)?.let { message ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                fontWeight = FontWeight.W300,
                lineHeight = 16.sp,
            )
        }
    }
}

/**
 * Executes the `formatSupporterDate` step in the SDAI presentation layer.
 *
 * @return Result produced by `formatSupporterDate`.
 * @author Dmitriy Moroz
 */
private fun String.formatSupporterDate(): String {
    val parts = split("-")
    if (parts.size != 3) return this
    val (year, month, day) = parts
    if (year.length != 4 || month.length != 2 || day.length != 2) return this
    return "$day.$month.$year"
}
