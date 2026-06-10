package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

@Composable
internal expect fun PlatformOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    containerColor: Color,
    textColor: Color,
    hintColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
)
