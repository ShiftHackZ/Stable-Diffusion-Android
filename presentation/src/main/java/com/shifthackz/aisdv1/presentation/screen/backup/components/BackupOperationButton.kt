package com.shifthackz.aisdv1.presentation.screen.backup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.screen.backup.BackupState
import kotlin.reflect.KClass
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun BackupOperationButton(
    state: BackupState,
    operationClass: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
//                color = if (state.operation == operationClass) MaterialTheme.colorScheme.primary
//                else Color.Transparent,
                color = state.operation
                    ?.takeIf { it::class.java.name == operationClass }
                    ?.let {
                        MaterialTheme.colorScheme.primary
                    } ?: Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
            .padding(bottom = 4.dp),
    ) {
        Row {
            Icon(
                modifier = Modifier
                    .size(42.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                imageVector = when (operationClass) {
                    BackupState.Operation.Create::class.java.name -> Icons.Default.Backup
                    BackupState.Operation.Restore::class.java.name -> Icons.Default.Restore
                    else -> Icons.Default.Restore
                },
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(top = 8.dp, bottom = 8.dp),
                text = stringResource(
                    id = when (operationClass) {
                        BackupState.Operation.Create::class.java.name-> LocalizationR.string.backup_operation_create
                        BackupState.Operation.Restore::class.java.name -> LocalizationR.string.backup_operation_restore
                        else -> LocalizationR.string.backup_operation_restore
                    }
                ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
