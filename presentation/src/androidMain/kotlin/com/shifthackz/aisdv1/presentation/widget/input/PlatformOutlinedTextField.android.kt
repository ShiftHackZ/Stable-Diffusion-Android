package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
internal actual fun PlatformOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    containerColor: Color,
    textColor: Color,
    hintColor: Color,
    modifier: Modifier,
    enabled: Boolean,
    error: String?,
    keyboardType: KeyboardType,
    visualTransformation: VisualTransformation,
    trailingIcon: @Composable (() -> Unit)?,
    singleLine: Boolean,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        enabled = enabled,
        isError = error != null,
        supportingText = error?.let {
            {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            disabledTextColor = textColor.copy(alpha = 0.38f),
            errorTextColor = textColor,
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = hintColor,
            disabledLabelColor = hintColor.copy(alpha = 0.38f),
            errorLabelColor = MaterialTheme.colorScheme.error,
            focusedPlaceholderColor = hintColor,
            unfocusedPlaceholderColor = hintColor,
            disabledPlaceholderColor = hintColor.copy(alpha = 0.38f),
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
    )
}
