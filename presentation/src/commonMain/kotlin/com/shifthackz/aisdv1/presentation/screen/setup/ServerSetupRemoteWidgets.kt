@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.widget.input.PlatformOutlinedTextField


/**
 * Renders the `FormTitle` UI for the SDAI presentation layer.
 *
 * @param title title value consumed by the API.
 * @param subtitle subtitle value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun FormTitle(
    title: String,
    subtitle: String? = null,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
    )
    subtitle?.let {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = it,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

/**
 * Renders the `HintText` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun HintText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.padding(top = 8.dp, bottom = 16.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
    )
}

/**
 * Renders the `AuthFields` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun AuthFields(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Text(
        modifier = Modifier.padding(top = 4.dp),
        text = strings.authTitle,
        style = MaterialTheme.typography.labelLarge,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ServerSetupState.AuthType.entries.forEach { type ->
            FilterChip(
                selected = state.authType == type,
                onClick = {
                    processIntent(ServerSetupIntent.UpdateAuthType(type))
                },
                label = {
                    Text(
                        text = when (type) {
                            ServerSetupState.AuthType.ANONYMOUS -> strings.anonymous
                            ServerSetupState.AuthType.HTTP_BASIC -> strings.httpBasic
                        },
                    )
                },
            )
        }
    }
    if (state.authType == ServerSetupState.AuthType.HTTP_BASIC) {
        SetupTextField(
            value = state.login,
            onValueChange = { processIntent(ServerSetupIntent.UpdateLogin(it)) },
            label = strings.login,
            error = state.loginValidationError?.message(strings),
        )
        SetupTextField(
            value = state.password,
            onValueChange = { processIntent(ServerSetupIntent.UpdatePassword(it)) },
            label = strings.password,
            error = state.passwordValidationError?.message(strings),
            keyboardType = KeyboardType.Password,
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val image = if (state.passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (state.passwordVisible) strings.hidePassword else strings.showPassword
                IconButton(
                    onClick = {
                        processIntent(
                            ServerSetupIntent.UpdatePasswordVisibility(
                                state.passwordVisible,
                            ),
                        )
                    },
                ) {
                    Icon(
                        imageVector = image,
                        contentDescription = description,
                    )
                }
            },
        )
    }
}

/**
 * Renders the `SetupTextField` UI for the SDAI presentation layer.
 *
 * @param value value value consumed by the API.
 * @param onValueChange callback invoked by the component.
 * @param label label value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param enabled enabled value consumed by the API.
 * @param error error value consumed by the API.
 * @param keyboardType keyboard type value consumed by the API.
 * @param visualTransformation visual transformation value consumed by the API.
 * @param trailingIcon trailing icon value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SetupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    PlatformOutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        value = value,
        onValueChange = onValueChange,
        label = label,
        containerColor = MaterialTheme.colorScheme.background,
        textColor = MaterialTheme.colorScheme.onSurface,
        hintColor = MaterialTheme.colorScheme.onSurfaceVariant,
        enabled = enabled,
        error = error,
        keyboardType = keyboardType,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = true,
    )
}

