package com.shifthackz.aisdv1.presentation.screen.backup.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.screen.backup.BackupIntent
import com.shifthackz.aisdv1.presentation.screen.backup.BackupState
import com.shifthackz.aisdv1.presentation.screen.backup.components.BackupOperationButton

@Composable
fun BackupOperationForm(
    state: BackupState,
    processIntent: (BackupIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        listOf(
            BackupState.Operation.Create::class.java.name,
            BackupState.Operation.Restore::class.java.name,
        ).forEach { item ->
            BackupOperationButton(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                state = state,
                operationClass = item,
            ) {
                processIntent(BackupIntent.SelectOperation(item))
            }
        }
    }
}
