package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.MaterialTheme
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
fun SwarmUiForm(
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
            text = stringResource(id = LocalizationR.string.hint_swarm_ui_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        val fieldModifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        TextField(
            modifier = fieldModifier,
            value = state.swarmUiUrl,
            onValueChange = {
                processIntent(ServerSetupIntent.UpdateSwarmUiUrl(it))
            },
            label = { Text(stringResource(id = LocalizationR.string.hint_server_url)) },
            isError = state.swarmUiUrlValidationError != null,
            supportingText = state.swarmUiUrlValidationError
                ?.let { { Text(it.asString(), color = MaterialTheme.colorScheme.error) } },
            maxLines = 1,
            colors = textFieldColors,
        )
        AuthCredentialsForm(
            state = state,
            processIntent = processIntent,
            modifier = fieldModifier,
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = LocalizationR.string.settings_item_instructions.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.SwarmUiInstructions) },
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_args_swarm_ui_warning),
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(LocalizationR.string.hint_valid_urls, "7801"),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
