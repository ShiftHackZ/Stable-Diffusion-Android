package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import com.shifthackz.android.core.mvi.MviIntent

interface BackupIntent : MviIntent {

    data object DismissModal : BackupIntent

    data object NavigateBack : BackupIntent

    data object MainButtonClick : BackupIntent

    data class SelectOperation(val value: String) : BackupIntent

    @Suppress("ArrayInDataClass")
    data class SelectRestore(
        val path: String,
        val bytes: ByteArray,
    ): BackupIntent

    data class ToggleBackupEntry(
        val entry: BackupEntryToken,
        val checked: Boolean,
    ) : BackupIntent

    enum class OnResult : BackupIntent { Success, Fail }
}
