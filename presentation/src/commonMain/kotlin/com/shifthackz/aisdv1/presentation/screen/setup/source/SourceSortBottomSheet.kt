package com.shifthackz.aisdv1.presentation.screen.setup.source

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

/**
 * Bottom sheet with provider list sort order options.
 *
 * @param state current selected sort order.
 * @param strings localized sort option labels.
 * @param processIntent sends sort changes back to the setup MVI pipeline.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SourceSortBottomSheet(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = strings.sourceSortTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        ServerSetupState.SourceSortOrder.entries.forEach { sortOrder ->
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                selected = state.sourceSortOrder == sortOrder,
                showChevron = false,
                startIcon = if (state.sourceSortOrder == sortOrder) Icons.Default.Check else null,
                text = sortOrder.mapToUi(strings).asUiText(),
                onClick = {
                    processIntent(ServerSetupIntent.UpdateSourceSortOrder(sortOrder))
                    processIntent(ServerSetupIntent.DismissDialog)
                },
            )
        }
    }
}
