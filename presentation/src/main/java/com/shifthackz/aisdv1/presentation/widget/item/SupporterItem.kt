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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.extensions.format
import com.shifthackz.aisdv1.domain.entity.Supporter

@Composable
@Preview
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
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = supporter?.name ?: "",
                fontWeight = FontWeight.W500,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = supporter?.date?.format("dd.MM.yyyy") ?: "",
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
