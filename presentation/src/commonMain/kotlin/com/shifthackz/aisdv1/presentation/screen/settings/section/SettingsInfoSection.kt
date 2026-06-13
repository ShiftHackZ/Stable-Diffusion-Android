@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings.section

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsState
import com.shifthackz.aisdv1.presentation.screen.settings.model.text
import com.shifthackz.aisdv1.presentation.widget.item.SettingsHeader
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

@Composable
internal fun SettingsInfoSection(
    state: SettingsState,
    headerModifier: Modifier,
    itemModifier: Modifier,
    processIntent: (SettingsIntent) -> Unit,
) {
        if (!state.onBoardingDemo) {
            SettingsHeader(
                modifier = headerModifier,
                loading = state.loading,
                text = text("settings_header_info"),
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.MonetizationOn,
                text = text("settings_item_donate"),
                onClick = { processIntent(SettingsIntent.Action.Donate) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.AllInclusive,
                text = text("settings_item_on_boarding"),
                onClick = { processIntent(SettingsIntent.Action.OnBoarding) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Report,
                text = text("settings_item_report_problem"),
                onClick = { processIntent(SettingsIntent.Action.ReportProblem) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Policy,
                text = text("settings_item_policy"),
                onClick = { processIntent(SettingsIntent.LaunchUrl.OpenPolicy) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Gavel,
                text = text("settings_item_license"),
                onClick = { processIntent(SettingsIntent.LaunchUrl.OpenLicense) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Code,
                text = text("settings_item_source"),
                onClick = { processIntent(SettingsIntent.LaunchUrl.OpenSourceCode) },
            )
        }
}
