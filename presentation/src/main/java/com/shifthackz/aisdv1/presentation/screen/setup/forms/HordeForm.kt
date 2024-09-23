package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun HordeForm(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_server_horde_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = LocalizationR.string.hint_server_horde_sub_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = if (state.hordeDefaultApiKey) Constants.HORDE_DEFAULT_API_KEY else state.hordeApiKey,
            onValueChange = {
                processIntent(ServerSetupIntent.UpdateHordeApiKey(it))
            },
            label = { Text(stringResource(id = LocalizationR.string.hint_server_horde_api_key)) },
            enabled = !state.hordeDefaultApiKey,
            isError = state.hordeApiKeyValidationError != null && !state.hordeDefaultApiKey,
            supportingText = {
                state.hordeApiKeyValidationError
                    ?.takeIf { !state.hordeDefaultApiKey }
                    ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
            },
            colors = textFieldColors,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = state.hordeDefaultApiKey,
                onCheckedChange = {
                    processIntent(ServerSetupIntent.UpdateHordeDefaultApiKey(it))
                },
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = LocalizationR.string.hint_server_horde_use_default_api_key),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = LocalizationR.string.hint_server_horde_about.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.HordeInfo) },
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.Api,
            text = LocalizationR.string.hint_server_horde_get_api_key.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.HordeSignUp) },
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_server_horde_usage),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}