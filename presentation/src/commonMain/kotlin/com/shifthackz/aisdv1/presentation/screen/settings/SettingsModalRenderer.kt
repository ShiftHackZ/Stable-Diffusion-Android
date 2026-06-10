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

@Composable
internal fun SettingsModalRenderer(
    screenModal: SettingsModal,
    platformActions: SettingsPlatformActions,
    processIntent: (SettingsIntent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        processIntent(SettingsIntent.DismissDialog)
    }
    when (screenModal) {
        SettingsModal.None -> Unit
        SettingsModal.Communicating -> ProgressDialog(
            canDismiss = false,
        )

        SettingsModal.ClearAppCache -> DecisionInteractiveDialog(
            title = text("title_clear_app_cache"),
            text = text("interaction_cache_sub_title"),
            confirmActionText = text("yes"),
            dismissActionText = text("no"),
            onDismissRequest = dismiss,
            onConfirmAction = { processIntent(SettingsIntent.Action.ClearAppCache.Confirm) },
        )

        is SettingsModal.SelectSdModel -> {
            androidx.compose.runtime.key(screenModal.models, screenModal.selected) {
                var selectedItem = remember(screenModal.selected) {
                    androidx.compose.runtime.mutableStateOf(screenModal.selected)
                }
                DecisionInteractiveDialog(
                    title = text("title_select_sd_model"),
                    text = UiText.empty,
                    confirmActionText = text("action_select"),
                    onConfirmAction = {
                        processIntent(SettingsIntent.SdModel.Select(selectedItem.value))
                    },
                    onDismissRequest = dismiss,
                    content = {
                        DropdownTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = text("hint_sd_model"),
                            value = selectedItem.value,
                            items = screenModal.models,
                            onItemSelected = { selectedItem.value = it },
                        )
                    },
                )
            }
        }

        SettingsModal.Language -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            LanguageBottomSheet(
                onLanguageSelected = { processIntent(SettingsIntent.Action.SetLanguage(it)) },
                onDismissRequest = dismiss,
            )
        }

        is SettingsModal.GalleryGrid -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            GridBottomSheet(
                currentGrid = screenModal.grid,
                onSelected = {
                    processIntent(SettingsIntent.Action.GalleryGrid.Set(it))
                    dismiss()
                },
            )
        }

        is SettingsModal.ManualPermission -> InfoDialog(
            title = text("premission_rationale_title"),
            subTitle = Localization.string(
                "premission_rationale_sub_title",
                screenModal.permission,
            ).asUiText(),
            onDismissRequest = {
                dismiss()
                scope.launch { platformActions.openAppSettings() }
            },
        )
    }
}
