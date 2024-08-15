package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.android.core.mvi.MviIntent

interface BackupIntent : MviIntent {

    data object NavigateBack : BackupIntent
}
