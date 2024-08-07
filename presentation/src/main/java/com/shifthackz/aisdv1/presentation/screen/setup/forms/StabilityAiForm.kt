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
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun StabilityAiForm(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_stability_ai_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = LocalizationR.string.hint_stability_ai_sub_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = state.stabilityAiApiKey,
            onValueChange = {
                processIntent(ServerSetupIntent.UpdateStabilityAiApiKey(it))
            },
            label = { Text(stringResource(id = LocalizationR.string.hint_server_horde_api_key)) },
            isError = state.stabilityAiApiKeyValidationError != null,
            supportingText = {
                state.stabilityAiApiKeyValidationError
                    ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
            },
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = LocalizationR.string.hint_stability_ai_about.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.StabilityAiInfo) },
        )
    }
}
