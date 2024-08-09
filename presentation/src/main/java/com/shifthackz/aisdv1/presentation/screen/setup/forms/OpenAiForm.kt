@file:OptIn(ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.forms

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.shifthackz.aisdv1.core.localization.R as LocalizationR


@Composable
fun OpenAiForm(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = LocalizationR.string.hint_open_ai_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = LocalizationR.string.hint_open_ai_sub_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        TextField(
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            delay(400L)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            value = state.openAiApiKey,
            onValueChange = {
                processIntent(ServerSetupIntent.UpdateOpenAiApiKey(it))
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
            label = { Text(stringResource(id = LocalizationR.string.hint_server_horde_api_key)) },
            isError = state.openAiApiKeyValidationError != null,
            supportingText = {
                state.openAiApiKeyValidationError
                    ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
            },
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = LocalizationR.string.hint_open_ai_about.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl.OpenAiInfo) },
        )
    }
}
