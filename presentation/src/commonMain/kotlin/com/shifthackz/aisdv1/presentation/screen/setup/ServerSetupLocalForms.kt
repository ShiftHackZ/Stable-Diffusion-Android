package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar

@Composable
internal fun ConfigurationStep(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    listState: LazyListState,
    processIntent: (ServerSetupIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.verticalScrollbar(listState),
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            when (state.mode) {
                ServerSource.AUTOMATIC1111 -> Automatic1111Form(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )

                ServerSource.SWARM_UI -> SwarmUiForm(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )

                ServerSource.HORDE -> HordeForm(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )

                ServerSource.HUGGING_FACE -> HuggingFaceForm(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )

                ServerSource.OPEN_AI -> OpenAiForm(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )

                ServerSource.STABILITY_AI -> StabilityAiForm(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )

                ServerSource.LOCAL_MICROSOFT_ONNX,
                ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> LocalGenerationForm(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )
            }
        }
    }
}

@Composable
internal fun LocalGenerationForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FormTitle(
            title = when (state.mode) {
                ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> strings.mediaPipeTitle
                else -> strings.localDiffusionTitle
            },
            subtitle = when (state.mode) {
                ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> strings.mediaPipeSubtitle
                else -> strings.localDiffusionSubtitle
            },
        )
        HintText(text = strings.localWarning)
        if (!isLocalGenerationSetupAvailable()) {
            SettingsItem(
                modifier = Modifier.fillMaxWidth(),
                startIcon = Icons.Default.Computer,
                text = strings.localPermissionHeader.asUiText(),
                endValueText = strings.localPermissionTitle.asUiText(),
            )
        }
        SettingsItem(
            modifier = Modifier.fillMaxWidth(),
            startIcon = Icons.Outlined.Landslide,
            text = strings.localCustomSwitch.asUiText(),
            onClick = { processIntent(ServerSetupIntent.AllowLocalCustomModel(!state.localCustomModel)) },
            endValueContent = {
                Switch(
                    checked = state.localCustomModel,
                    onCheckedChange = { processIntent(ServerSetupIntent.AllowLocalCustomModel(it)) },
                )
            },
        )
        if (state.localCustomModel) {
            LocalCustomModelPathForm(
                state = state,
                strings = strings,
                processIntent = processIntent,
            )
        }
        state.localModels.forEach { model ->
            LocalModelItem(
                model = model,
                selected = model.selected,
                strings = strings,
                onSelect = { processIntent(ServerSetupIntent.SelectLocalModel(model)) },
                onAction = { processIntent(ServerSetupIntent.LocalModel.ClickReduce(model)) },
            )
        }
    }
}

@Composable
internal fun LocalCustomModelPathForm(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SetupTextField(
            value = state.localCustomModelPath,
            onValueChange = { processIntent(ServerSetupIntent.SelectLocalModelPath(it)) },
            label = strings.localPathHeader,
            error = state.localCustomModelPathValidationError?.message(strings),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ServerSetupLocalPathPickerButton(
                modifier = Modifier.weight(1f),
                text = strings.localPathButton,
                onPathSelected = { processIntent(ServerSetupIntent.SelectLocalModelPath(it)) },
            )
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    processIntent(
                        ServerSetupIntent.SelectLocalModelPath(
                            when (state.mode) {
                                ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> LOCAL_DIFFUSION_CUSTOM_PATH
                                else -> LOCAL_DIFFUSION_CUSTOM_PATH
                            },
                        ),
                    )
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = strings.localPathTitle,
                )
            }
        }
    }
}
