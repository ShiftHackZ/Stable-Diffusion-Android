@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar

/**
 * Renders the `ContentSettingsState` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ContentSettingsState(
    modifier: Modifier = Modifier,
    state: SettingsState,
    processIntent: (SettingsIntent) -> Unit = {},
) {
    val isDark = if (state.useSystemDarkTheme) {
        isSystemInDarkTheme()
    } else {
        state.darkTheme
    }
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .verticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp),
    ) {
        val headerModifier = Modifier.padding(top = 28.dp, bottom = 8.dp)
        val itemModifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        val warningModifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 4.dp)

        SettingsServerSection(
            state = state,
            headerModifier = headerModifier,
            itemModifier = itemModifier,
            warningModifier = warningModifier,
            processIntent = processIntent,
        )
        SettingsLookAndFeelSection(
            state = state,
            isDark = isDark,
            headerModifier = headerModifier,
            itemModifier = itemModifier,
            processIntent = processIntent,
        )
        SettingsCommunitySection(
            state = state,
            headerModifier = headerModifier,
            itemModifier = itemModifier,
            processIntent = processIntent,
        )
        SettingsInfoSection(
            state = state,
            headerModifier = headerModifier,
            itemModifier = itemModifier,
            processIntent = processIntent,
        )
        SettingsVersionFooter(
            state = state,
            processIntent = processIntent,
        )
    }
}
