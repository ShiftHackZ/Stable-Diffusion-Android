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
 * Renders the `SettingsLookAndFeelSection` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param isDark is dark value consumed by the API.
 * @param headerModifier header modifier value consumed by the API.
 * @param itemModifier item modifier value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SettingsLookAndFeelSection(
    state: SettingsState,
    isDark: Boolean,
    headerModifier: Modifier,
    itemModifier: Modifier,
    processIntent: (SettingsIntent) -> Unit,
) {
        SettingsHeader(
            modifier = headerModifier,
            loading = state.loading,
            text = text("settings_header_look_and_feel"),
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Translate,
            text = text("settings_item_lf_lang"),
            endValueText = text("language"),
            onClick = { processIntent(SettingsIntent.Action.PickLanguage) },
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            text = text("settings_item_lf_gallery_grid"),
            endValueText = state.galleryGrid.size.toString().asUiText(),
            startIconContent = {
                GridIcon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    grid = state.galleryGrid,
                    color = LocalContentColor.current,
                )
            },
            onClick = { processIntent(SettingsIntent.Action.GalleryGrid.Pick) },
        )
        if (state.showUseSystemColorPalette) {
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Save,
                text = text("settings_item_lf_dynamic_colors"),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.DynamicColors(!state.useSystemColorPalette))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.useSystemColorPalette,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.DynamicColors(it))
                        },
                    )
                },
            )
        }
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.InvertColors,
            text = text("settings_item_lf_system_dark_theme"),
            onClick = {
                processIntent(SettingsIntent.UpdateFlag.SystemDarkTheme(!state.useSystemDarkTheme))
            },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.useSystemDarkTheme,
                    onCheckedChange = {
                        processIntent(SettingsIntent.UpdateFlag.SystemDarkTheme(it))
                    },
                )
            },
        )
        AnimatedVisibility(visible = !state.useSystemDarkTheme) {
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.DarkMode,
                text = text("settings_item_lf_dark_theme"),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.DarkTheme(!state.darkTheme))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.darkTheme,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.DarkTheme(it))
                        },
                    )
                },
            )
        }
        AnimatedVisibility(visible = !state.loading && isDark) {
            Column(
                modifier = itemModifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsItemContent(
                    text = text("settings_item_lf_dark_theme_color"),
                    icon = Icons.Default.FormatColorFill,
                )
                Spacer(modifier = Modifier.height(16.dp))
                DarkThemeColorSelector(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    selectedToken = state.darkThemeToken,
                    colorToken = state.colorToken,
                    onSelected = { _, token ->
                        processIntent(SettingsIntent.NewDarkThemeToken(token))
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
        AnimatedVisibility(visible = !state.loading) {
            Column(
                modifier = itemModifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsItemContent(
                    text = text("settings_item_lf_accent_color"),
                    icon = Icons.Default.ColorLens,
                )
                Spacer(modifier = Modifier.height(16.dp))
                AccentColorSelector(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    isDark = isDark,
                    darkThemeToken = state.darkThemeToken,
                    selectedToken = state.colorToken,
                    onSelected = { _, token ->
                        processIntent(SettingsIntent.NewColorToken(token))
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
}

