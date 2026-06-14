package com.shifthackz.aisdv1.presentation.screen.storageusage.model

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.presentation.model.UsageItem
import com.shifthackz.aisdv1.presentation.model.UsageState

/**
 * State for the standalone storage usage screen.
 *
 * @param usage Shared dashboard state with storage categories and loading flag.
 * @param screenModal Confirmation dialog state for destructive storage actions.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class StorageUsageState(
    /**
     * Shared usage dashboard state with storage categories and loading flag.
     *
     * @author Dmitriy Moroz
     */
    val usage: UsageState = UsageState(loading = true),

    /**
     * Confirmation dialog state for destructive storage actions.
     *
     * @author Dmitriy Moroz
     */
    val screenModal: StorageUsageModal = StorageUsageModal.None,
) : MviState

/**
 * Destructive action dialogs shown by the storage usage screen.
 *
 * @author Dmitriy Moroz
 */
sealed interface StorageUsageModal {
    /**
     * No dialog is visible.
     *
     * @author Dmitriy Moroz
     */
    data object None : StorageUsageModal

    /**
     * Confirmation for deleting one category.
     *
     * @param item Non-empty storage row whose bytes and label are shown in the dialog.
     *
     * @author Dmitriy Moroz
     */
    data class ClearCategory(val item: UsageItem) : StorageUsageModal

    /**
     * Confirmation for deleting every currently non-empty storage category.
     *
     * @param items Non-empty storage rows that will be deleted if the user confirms.
     * @param totalBytes Sum of [items] shown in the destructive confirmation copy.
     *
     * @author Dmitriy Moroz
     */
    data class ClearAll(
        val items: List<UsageItem>,
        val totalBytes: Long,
    ) : StorageUsageModal
}
