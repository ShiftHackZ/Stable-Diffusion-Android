package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

@Composable
fun Automatic1111Form(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = R.string.hint_server_setup_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        val fieldModifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        TextField(
            modifier = fieldModifier,
            value = if (!state.demoMode) state.serverUrl else state.demoModeUrl,
            onValueChange = {
                processIntent(ServerSetupIntent.UpdateServerUrl(it))
            },
            label = { Text(stringResource(id = R.string.hint_server_url)) },
            enabled = !state.demoMode,
            isError = state.serverUrlValidationError != null && !state.demoMode,
            supportingText = state.serverUrlValidationError
                ?.takeIf { !state.demoMode }
                ?.let { { Text(it.asString(), color = MaterialTheme.colorScheme.error) } },
        )
        if (!state.demoMode) {
            DropdownTextField(
                modifier = fieldModifier,
                label = "Authorization".asUiText(),
                items = ServerSetupState.AuthType.entries,
                value = state.authType,
                onItemSelected = {
                    processIntent(ServerSetupIntent.UpdateAuthType(it))
                },
                displayDelegate = { type ->
                    when (type) {
                        ServerSetupState.AuthType.ANONYMOUS -> R.string.auth_anonymous
                        ServerSetupState.AuthType.HTTP_BASIC -> R.string.auth_http_basic
                    }.asUiText()
                }
            )
            when (state.authType) {
                ServerSetupState.AuthType.HTTP_BASIC -> {

                    TextField(
                        modifier = fieldModifier,
                        value = state.login,
                        onValueChange = {
                            processIntent(ServerSetupIntent.UpdateLogin(it))
                        },
                        label = { Text(stringResource(id = R.string.hint_login)) },
                        isError = state.loginValidationError != null,
                        supportingText = state.loginValidationError?.let {
                            { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
                        },
                    )
                    TextField(
                        modifier = fieldModifier,
                        value = state.password,
                        onValueChange = {
                            processIntent(ServerSetupIntent.UpdatePassword(it))
                        },
                        label = { Text(stringResource(id = R.string.hint_password)) },
                        isError = state.passwordValidationError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (state.passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
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
                        }
                    )
                }
                else -> Unit
            }
        }
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = R.string.settings_item_instructions.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.A1111Instructions) },
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.DeveloperMode,
            text = R.string.settings_item_demo.asUiText(),
            onClick = { processIntent(ServerSetupIntent.UpdateDemoMode(!state.demoMode)) },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.demoMode,
                    onCheckedChange = {
                        processIntent(ServerSetupIntent.UpdateDemoMode(it))
                    },
                )
            }
        )
        if (!state.demoMode) Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = R.string.hint_args_warning),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(
                if (state.demoMode) R.string.hint_demo_mode
                else R.string.hint_valid_urls,
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}