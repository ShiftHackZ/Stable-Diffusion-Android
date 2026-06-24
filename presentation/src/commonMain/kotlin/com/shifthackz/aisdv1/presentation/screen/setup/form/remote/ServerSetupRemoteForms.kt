package com.shifthackz.aisdv1.presentation.screen.setup.form.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.screen.setup.component.SettingsAction
import com.shifthackz.aisdv1.presentation.screen.setup.component.SwitchRow
import com.shifthackz.aisdv1.presentation.screen.setup.component.message
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupLink
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

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

@Composable
internal fun FalAiForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.falAiTitle,
        subtitle = strings.falAiSubtitle,
    ) {
        SetupTextField(
            value = state.falAiApiKey,
            onValueChange = { processIntent(ServerSetupIntent.UpdateFalAiApiKey(it)) },
            label = strings.apiKey,
            keyboardType = KeyboardType.Password,
            error = state.falAiApiKeyValidationError?.message(strings),
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Cloud,
            text = strings.falAiAbout.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.FalAiInfo)) },
        )
    }
}

@Composable
internal fun ArliAiForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    RemoteFormScaffold(
        title = strings.arliAiTitle,
        subtitle = strings.arliAiSubtitle,
    ) {
        SetupTextField(
            value = state.arliAiApiKey,
            onValueChange = { processIntent(ServerSetupIntent.UpdateArliAiApiKey(it)) },
            label = strings.apiKey,
            keyboardType = KeyboardType.Password,
            error = state.arliAiApiKeyValidationError?.message(strings),
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            startIcon = Icons.Default.Cloud,
            text = strings.arliAiAbout.asUiText(),
            onClick = { processIntent(ServerSetupIntent.LaunchUrl(ServerSetupLink.ArliAiInfo)) },
        )
    }
}

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

@Composable
internal fun SdaiCloudForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormTitle(title = strings.sdaiCloudTitle)
        when {
            state.sdaiCloudTermsLoading -> SdaiCloudTermsLoading(strings = strings)
            state.sdaiCloudTermsVersion.isBlank() || state.sdaiCloudTermsLoadFailed -> SdaiCloudTermsError(
                strings = strings,
                processIntent = processIntent,
            )

            else -> SdaiCloudTermsDocument(state = state)
        }
    }
}

@Composable
private fun SdaiCloudTermsLoading(
    strings: ServerSetupStrings,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier.padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                CircularProgressIndicator()
                Text(
                    text = strings.sdaiCloudTermsLoading,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun SdaiCloudTermsError(
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 260.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Cloud,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
            )
            Text(
                text = strings.sdaiCloudTermsUnavailableTitle,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = strings.sdaiCloudTermsUnavailable,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = { processIntent(ServerSetupIntent.RetrySdaiCloudTerms) },
            ) {
                Text(text = strings.retry)
            }
        }
    }
}

@Composable
private fun SdaiCloudTermsDocument(
    state: ServerSetupState,
) {
    val blocks = remember(state.sdaiCloudTermsBody) {
        state.sdaiCloudTermsBody.toTermsBlocks()
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 420.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (state.sdaiCloudTermsTitle.isNotBlank()) {
                    Text(
                        text = state.sdaiCloudTermsTitle,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (state.sdaiCloudTermsVersion.isNotBlank()) {
                    Text(
                        text = state.sdaiCloudTermsVersion,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                blocks.forEach { block ->
                    when (block) {
                        is TermsBlock.Heading -> Text(
                            text = block.text,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = if (block.level <= 1) {
                                MaterialTheme.typography.titleMedium
                            } else {
                                MaterialTheme.typography.titleSmall
                            },
                        )

                        is TermsBlock.Paragraph -> Text(
                            text = block.text,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        is TermsBlock.Bullets -> Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            block.items.forEach { item ->
                                Text(
                                    text = "- $item",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed interface TermsBlock {
    data class Heading(
        val level: Int,
        val text: String,
    ) : TermsBlock

    data class Paragraph(
        val text: String,
    ) : TermsBlock

    data class Bullets(
        val items: List<String>,
    ) : TermsBlock
}

private fun String.toTermsBlocks(): List<TermsBlock> {
    val blocks = mutableListOf<TermsBlock>()
    val paragraph = mutableListOf<String>()
    var bullets = mutableListOf<String>()

    fun flushParagraph() {
        if (paragraph.isEmpty()) return
        blocks += TermsBlock.Paragraph(paragraph.joinToString(" "))
        paragraph.clear()
    }

    fun flushBullets() {
        if (bullets.isEmpty()) return
        blocks += TermsBlock.Bullets(bullets.toList())
        bullets = mutableListOf()
    }

    lineSequence()
        .map(String::trim)
        .forEach { line ->
            when {
                line.isBlank() -> {
                    flushParagraph()
                    flushBullets()
                }

                line.startsWith("#") -> {
                    flushParagraph()
                    flushBullets()
                    val level = line.takeWhile { char -> char == '#' }.length
                    blocks += TermsBlock.Heading(
                        level = level,
                        text = line.drop(level).trim(),
                    )
                }

                line.startsWith("- ") || line.startsWith("* ") -> {
                    flushParagraph()
                    bullets += line.drop(2).trim()
                }

                else -> {
                    flushBullets()
                    paragraph += line
                }
            }
        }

    flushParagraph()
    flushBullets()

    return blocks
}

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
