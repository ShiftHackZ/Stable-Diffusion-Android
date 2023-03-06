@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText

@Composable
fun <T : Any> DropdownTextField(
    modifier: Modifier = Modifier,
    label: UiText = UiText.empty,
    value: T,
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
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = displayDelegate(value).asString(),
            onValueChange = {},
            readOnly = true,
            label = { Text(label.asString()) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
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
