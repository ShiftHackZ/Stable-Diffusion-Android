package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ColumnScope.AuthCredentialsForm(
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit,
    modifier: Modifier,
) {
    DropdownTextField(
        modifier = modifier,
        label = LocalizationR.string.auth_title.asUiText(),
        items = ServerSetupState.AuthType.entries,
        value = state.authType,
        onItemSelected = {
            processIntent(ServerSetupIntent.UpdateAuthType(it))
        },
        displayDelegate = { type ->
            when (type) {
                ServerSetupState.AuthType.ANONYMOUS -> LocalizationR.string.auth_anonymous
                ServerSetupState.AuthType.HTTP_BASIC -> LocalizationR.string.auth_http_basic
            }.asUiText()
        }
    )
    when (state.authType) {
        ServerSetupState.AuthType.HTTP_BASIC -> {
            TextField(
                modifier = modifier,
                value = state.login,
                onValueChange = {
                    processIntent(ServerSetupIntent.UpdateLogin(it))
                },
                label = { Text(stringResource(id = LocalizationR.string.hint_login)) },
                isError = state.loginValidationError != null,
                supportingText = state.loginValidationError?.let {
                    { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
                },
                maxLines = 1,
            )
            TextField(
                modifier = modifier,
                value = state.password,
                onValueChange = {
                    processIntent(ServerSetupIntent.UpdatePassword(it))
                },
                label = { Text(stringResource(id = LocalizationR.string.hint_password)) },
                isError = state.passwordValidationError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (state.passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                supportingText = state.passwordValidationError?.let {
                    { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
                },
                trailingIcon = {
                    val image = if (state.passwordVisible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff
                    val description = if (state.passwordVisible) "Hide password" else "Show password"
                    IconButton(
                        onClick = {
                            processIntent(
                                ServerSetupIntent.UpdatePasswordVisibility(
                                    state.passwordVisible,
                                ),
                            )
                        },
                        content = { Icon(image, description) },
                    )
                },
                maxLines = 1,
            )
        }
        else -> Unit
    }
}
