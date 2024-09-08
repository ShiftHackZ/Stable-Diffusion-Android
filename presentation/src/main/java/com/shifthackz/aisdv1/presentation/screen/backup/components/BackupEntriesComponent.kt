package com.shifthackz.aisdv1.presentation.screen.backup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.domain.entity.BackupEntryToken

@Composable
fun BackupEntriesComponent(
    modifier: Modifier = Modifier,
    tokens: List<Pair<BackupEntryToken, Boolean>> = listOf(),
    onCheckedChange: (BackupEntryToken, Boolean) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier,
    ) {
        tokens.forEach { (entry, checked) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        onCheckedChange(entry, it)
                    },
                )
                Text(
                    text = stringResource(
                        id = when (entry) {
                            BackupEntryToken.AppConfiguration -> LocalizationR.string.backup_entry_prefs
                            BackupEntryToken.Gallery -> LocalizationR.string.backup_entry_gallery
                        }
                    ),
                )
            }
        }
    }
}

@Composable
@Preview
private fun BackupEntriesComponentPreview() {
    Column {
        BackupEntriesComponent(
            tokens = BackupEntryToken.entries.map { it to false },
        )
    }
}
