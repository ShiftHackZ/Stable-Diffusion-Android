package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

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
            text = stringResource(id = LocalizationR.string.hint_server_setup_title),
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
            label = { Text(stringResource(id = LocalizationR.string.hint_server_url)) },
            enabled = !state.demoMode,
            isError = state.serverUrlValidationError != null && !state.demoMode,
            supportingText = state.serverUrlValidationError
                ?.takeIf { !state.demoMode }
                ?.let { { Text(it.asString(), color = MaterialTheme.colorScheme.error) } },
            maxLines = 1,
            colors = textFieldColors,
        )
        if (!state.demoMode) {
            AuthCredentialsForm(
                state = state,
                processIntent = processIntent,
                modifier = fieldModifier,
            )
        }
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = LocalizationR.string.settings_item_instructions.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.A1111Instructions) },
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.DeveloperMode,
            text = LocalizationR.string.settings_item_demo.asUiText(),
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
            text = stringResource(id = LocalizationR.string.hint_args_warning),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = if (state.demoMode) {
                stringResource(LocalizationR.string.hint_demo_mode)
            } else {
                stringResource(LocalizationR.string.hint_valid_urls, "7860")
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
