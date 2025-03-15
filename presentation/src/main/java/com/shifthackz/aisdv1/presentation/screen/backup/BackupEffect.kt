package com.shifthackz.aisdv1.presentation.screen.backup

import com.shifthackz.android.core.mvi.MviEffect

sealed interface BackupEffect : MviEffect {

    data class SaveBackup(val bytes: ByteArray) : BackupEffect {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as SaveBackup

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int {
            return bytes.contentHashCode()
        }
    }
}
