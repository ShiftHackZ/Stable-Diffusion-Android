@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.DynamicForm
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.modal.grid.GridBottomSheet
import com.shifthackz.aisdv1.presentation.modal.language.LanguageBottomSheet
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.widget.color.AccentColorSelector
import com.shifthackz.aisdv1.presentation.widget.color.DarkThemeColorSelector
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.InfoDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.icon.BrandIcons
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.GridIcon
import com.shifthackz.aisdv1.presentation.widget.item.SettingsHeader
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItemContent
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

/**
 * Renders the `SettingsServerSection` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param headerModifier header modifier value consumed by the API.
 * @param itemModifier item modifier value consumed by the API.
 * @param warningModifier warning modifier value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SettingsServerSection(
    state: SettingsState,
    headerModifier: Modifier,
    itemModifier: Modifier,
    warningModifier: Modifier,
    processIntent: (SettingsIntent) -> Unit,
) {
        if (!state.onBoardingDemo) {
            SettingsHeader(
                modifier = headerModifier,
                loading = state.loading,
                text = text("settings_header_server"),
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.SettingsEthernet,
                text = text("settings_item_config"),
                endValueText = state.serverSource.shortTitle().asUiText(),
                onClick = { processIntent(SettingsIntent.NavigateConfiguration) },
            )
            if (state.showStabilityAiCredits) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    enabled = false,
                    startIcon = Icons.Default.Circle,
                    text = text("settings_item_stability_ai_credits"),
                    endValueText = state.stabilityAiCredits.roundTo(4).toString().asUiText(),
                )
            }
            if (state.showSdModelSelector) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.AutoFixNormal,
                    text = text("settings_item_sd_model"),
                    endValueText = state.sdModelSelected.asUiText(),
                    onClick = { processIntent(SettingsIntent.SdModel.OpenChooser) },
                )
            }
            if (state.showLocalMicrosoftONNXUseNNAPI) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.AccountTree,
                    text = text("settings_item_local_nnapi"),
                    endValueText = state.sdModelSelected.asUiText(),
                    onClick = { processIntent(SettingsIntent.UpdateFlag.NNAPI(!state.localUseNNAPI)) },
                    endValueContent = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checked = state.localUseNNAPI,
                            onCheckedChange = { processIntent(SettingsIntent.UpdateFlag.NNAPI(it)) },
                        )
                    },
                )
                AnimatedVisibility(visible = !state.loading) {
                    Text(
                        modifier = warningModifier,
                        text = Localization.string("settings_item_local_nnapi_warning"),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            AnimatedVisibility(visible = state.loading || state.backgroundGenerationAvailable) {
                Column {
                    SettingsItem(
                        modifier = itemModifier,
                        loading = state.loading,
                        startIcon = Icons.Default.MiscellaneousServices,
                        text = text("settings_item_background_generation"),
                        onClick = {
                            processIntent(SettingsIntent.UpdateFlag.BackgroundGeneration(!state.backgroundGeneration))
                        },
                        endValueContent = {
                            Switch(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                checked = state.backgroundGeneration,
                                onCheckedChange = {
                                    processIntent(SettingsIntent.UpdateFlag.BackgroundGeneration(it))
                                },
                            )
                        },
                    )
                    AnimatedVisibility(visible = !state.loading) {
                        Text(
                            modifier = warningModifier,
                            text = Localization.string("settings_item_background_generation_warning"),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
            AnimatedVisibility(visible = !state.loading && state.developerMode) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.DeveloperMode,
                    text = text("title_debug_menu"),
                    onClick = { processIntent(SettingsIntent.NavigateDeveloperMode) },
                )
            }

            SettingsHeader(
                modifier = headerModifier,
                loading = state.loading,
                text = text("settings_header_app"),
            )
            if (state.showMonitorConnectionOption) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.Refresh,
                    text = text("settings_item_monitor_connection"),
                    onClick = {
                        processIntent(SettingsIntent.UpdateFlag.MonitorConnection(!state.monitorConnectivity))
                    },
                    endValueContent = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checked = state.monitorConnectivity,
                            onCheckedChange = {
                                processIntent(SettingsIntent.UpdateFlag.MonitorConnection(it))
                            },
                        )
                    },
                )
            }
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Save,
                text = (Localization.string("settings_item_auto_save") +
                    if (state.backgroundGeneration) "*" else "").asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.AutoSaveResult(!state.autoSaveAiResults))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.autoSaveAiResults,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.AutoSaveResult(it))
                        },
                    )
                },
            )
            AnimatedVisibility(
                visible = !state.loading && state.backgroundGeneration,
            ) {
                Text(
                    modifier = warningModifier,
                    text = Localization.string("settings_item_auto_save_warning"),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Folder,
                text = text("settings_item_auto_save_media_store"),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(!state.saveToMediaStore))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.saveToMediaStore,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(it))
                        },
                    )
                },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Tag,
                text = text("settings_item_tagged_input"),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.TaggedInput(!state.formPromptTaggedInput))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.formPromptTaggedInput,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.TaggedInput(it))
                        },
                    )
                },
            )
            if (state.showFormAdvancedOption) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.DynamicForm,
                    text = text("settings_item_advanced_form_default"),
                    onClick = {
                        processIntent(SettingsIntent.UpdateFlag.AdvancedFormVisibility(!state.formAdvancedOptionsAlwaysShow))
                    },
                    endValueContent = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checked = state.formAdvancedOptionsAlwaysShow,
                            onCheckedChange = {
                                processIntent(SettingsIntent.UpdateFlag.AdvancedFormVisibility(it))
                            },
                        )
                    },
                )
            }
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.DeleteForever,
                text = text("settings_item_clear_cache"),
                onClick = { processIntent(SettingsIntent.Action.ClearAppCache.Request) },
            )
        }
}

