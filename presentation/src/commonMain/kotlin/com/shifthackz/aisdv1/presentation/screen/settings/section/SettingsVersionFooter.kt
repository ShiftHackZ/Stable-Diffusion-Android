@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsState

@Composable
internal fun SettingsVersionFooter(
    state: SettingsState,
    processIntent: (SettingsIntent) -> Unit,
) {
        AnimatedVisibility(visible = state.appVersion.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { processIntent(SettingsIntent.Action.AppVersion) },
                    ),
                text = Localization.string("version", state.appVersion) +
                    "\n" + Localization.string("settings_footer_developed_by"),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
}
