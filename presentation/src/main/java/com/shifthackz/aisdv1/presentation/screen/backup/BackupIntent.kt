package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import com.shifthackz.android.core.mvi.MviIntent

interface BackupIntent : MviIntent {

    data object NavigateBack : BackupIntent

    data object MainButtonClick : BackupIntent

    data class SelectOperation(val value: String) : BackupIntent

    data class ToggleBackupEntry(
        val entry: BackupEntryToken,
        val checked: Boolean,
    ) : BackupIntent
}
