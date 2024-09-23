package com.shifthackz.aisdv1.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

val textFieldColors: TextFieldColors
    @Composable get() = TextFieldDefaults.colors(
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
    )
