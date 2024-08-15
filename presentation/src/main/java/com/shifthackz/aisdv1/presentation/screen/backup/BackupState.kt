package com.shifthackz.aisdv1.presentation.screen.backup

import androidx.compose.runtime.Immutable
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class BackupState(
    val s: Int = -1,
) : MviState
