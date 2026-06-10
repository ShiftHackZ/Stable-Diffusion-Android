package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

/**
 * Renders the `Automatic1111Form` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun Automatic1111Form(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.automaticFormTitle,
        subtitle = strings.automaticSubtitle,
    ) {
        SetupTextField(
            value = if (state.demoMode) state.demoModeUrl else state.serverUrl,
            onValueChange = { processIntent(ServerSetupIntent.UpdateServerUrl(it)) },
            label = strings.serverUrl,
            enabled = !state.demoMode,
            error = state.serverUrlValidationError?.message(strings),
        )
        AuthFields(
            state = state,
            strings = strings,
            processIntent = processIntent,
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = strings.instructions.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.A1111Instructions)) },
        )
        SwitchRow(
            icon = Icons.Default.Api,
            text = strings.demoMode,
            checked = state.demoMode,
            onCheckedChange = { processIntent(ServerSetupIntent.UpdateDemoMode(it)) },
        )
        HintText(text = if (state.demoMode) strings.demoHint else strings.automaticHint)
    }
}

/**
 * Renders the `SwarmUiForm` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SwarmUiForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.swarmFormTitle,
        subtitle = strings.swarmSubtitle,
    ) {
        SetupTextField(
            value = state.swarmUiUrl,
            onValueChange = { processIntent(ServerSetupIntent.UpdateSwarmUiUrl(it)) },
            label = strings.serverUrl,
            error = state.swarmUiUrlValidationError?.message(strings),
        )
        AuthFields(
            state = state,
            strings = strings,
            processIntent = processIntent,
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = strings.instructions.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.SwarmUiInstructions)) },
        )
        HintText(text = strings.swarmHint)
    }
}

/**
 * Renders the `HordeForm` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun HordeForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.hordeTitle,
        subtitle = strings.hordeSubtitle,
    ) {
        SetupTextField(
            value = state.hordeApiKey,
            onValueChange = { processIntent(ServerSetupIntent.UpdateHordeApiKey(it)) },
            label = strings.apiKey,
            enabled = !state.hordeDefaultApiKey,
            keyboardType = KeyboardType.Password,
            error = state.hordeApiKeyValidationError?.message(strings),
        )
        SwitchRow(
            icon = Icons.Default.Api,
            text = strings.useDefaultHordeKey,
            checked = state.hordeDefaultApiKey,
            onCheckedChange = { processIntent(ServerSetupIntent.UpdateHordeDefaultApiKey(it)) },
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Cloud,
            text = strings.hordeAbout.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.HordeInfo)) },
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Api,
            text = strings.hordeGetKey.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.HordeSignUp)) },
        )
        HintText(text = strings.hordeUsage)
    }
}

/**
 * Renders the `HuggingFaceForm` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun HuggingFaceForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.huggingFaceTitle,
        subtitle = strings.huggingFaceSubtitle,
    ) {
        SetupTextField(
            value = state.huggingFaceApiKey,
            onValueChange = { processIntent(ServerSetupIntent.UpdateHuggingFaceApiKey(it)) },
            label = strings.apiKey,
            keyboardType = KeyboardType.Password,
            error = state.huggingFaceApiKeyValidationError?.message(strings),
        )
        DropdownTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = strings.model.asUiText(),
            value = state.huggingFaceModel,
            items = state.huggingFaceModels,
            onItemSelected = { processIntent(ServerSetupIntent.UpdateHuggingFaceModel(it)) },
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Cloud,
            text = strings.huggingFaceAbout.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.HuggingFaceInfo)) },
        )
    }
}

/**
 * Renders the `OpenAiForm` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun OpenAiForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.openAiTitle,
        subtitle = strings.openAiSubtitle,
    ) {
        SetupTextField(
            value = state.openAiApiKey,
            onValueChange = { processIntent(ServerSetupIntent.UpdateOpenAiApiKey(it)) },
            label = strings.apiKey,
            keyboardType = KeyboardType.Password,
            error = state.openAiApiKeyValidationError?.message(strings),
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Cloud,
            text = strings.openAiAbout.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.OpenAiInfo)) },
        )
    }
}

/**
 * Renders the `StabilityAiForm` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun StabilityAiForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.stabilityTitle,
        subtitle = strings.stabilitySubtitle,
    ) {
        SetupTextField(
            value = state.stabilityAiApiKey,
            onValueChange = { processIntent(ServerSetupIntent.UpdateStabilityAiApiKey(it)) },
            label = strings.apiKey,
            keyboardType = KeyboardType.Password,
            error = state.stabilityAiApiKeyValidationError?.message(strings),
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Cloud,
            text = strings.stabilityAbout.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.StabilityAiInfo)) },
        )
    }
}

/**
 * Renders the `RemoteFormScaffold` UI for the SDAI presentation layer.
 *
 * @param title title value consumed by the API.
 * @param subtitle subtitle value consumed by the API.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun RemoteFormScaffold(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FormTitle(
            title = title,
            subtitle = subtitle,
        )
        content()
    }
}
