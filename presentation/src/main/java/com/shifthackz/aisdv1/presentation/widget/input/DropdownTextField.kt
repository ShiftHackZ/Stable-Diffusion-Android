@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.theme.textFieldColors

@Composable
fun <T : Any> DropdownTextField(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    label: UiText = UiText.empty,
    value: T?,
    items: List<T> = emptyList(),
    onItemSelected: (T) -> Unit = {},
    displayDelegate: (T) -> UiText = { t -> t.toString().asUiText() },
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        if (!loading) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                value = value?.let { displayDelegate(it).asString() } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(label.asString()) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = textFieldColors,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmer()
            )
        }
        ExposedDropdownMenu(
            expanded = expanded && !loading,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(displayDelegate(item).asString()) },
                    onClick = {
                        expanded = false
                        if (value == item) return@DropdownMenuItem
                        onItemSelected(item)
                    },
                )
            }
        }
    }
}
