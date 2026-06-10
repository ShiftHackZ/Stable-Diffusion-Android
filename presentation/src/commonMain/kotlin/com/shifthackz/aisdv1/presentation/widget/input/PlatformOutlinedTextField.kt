package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Renders the `PlatformOutlinedTextField` UI for the SDAI presentation layer.
 *
 * @param value value value consumed by the API.
 * @param onValueChange callback invoked by the component.
 * @param label label value consumed by the API.
 * @param containerColor container color value consumed by the API.
 * @param textColor text color value consumed by the API.
 * @param hintColor hint color value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param enabled enabled value consumed by the API.
 * @param error error value consumed by the API.
 * @param keyboardType keyboard type value consumed by the API.
 * @param visualTransformation visual transformation value consumed by the API.
 * @param trailingIcon trailing icon value consumed by the API.
 * @param singleLine single line value consumed by the API.
 * @author Dmitriy Moroz
 */
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
