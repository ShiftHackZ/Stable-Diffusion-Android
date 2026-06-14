package com.shifthackz.aisdv1.presentation.screen.storageusage.model

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.presentation.model.UsageCategory

/**
 * User actions supported by the standalone storage usage screen.
 *
 * @author Dmitriy Moroz
 */
sealed interface StorageUsageIntent : MviIntent {
    /**
     * Close the screen and return to Settings.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : StorageUsageIntent

    /**
     * Highlight a storage category in the donut chart and center label.
     *
     * @param category Storage category selected by tapping the row or donut segment.
     *
     * @author Dmitriy Moroz
     */
    data class SelectCategory(val category: UsageCategory) : StorageUsageIntent

    /**
     * Ask the UI to confirm deleting one non-empty category.
     *
     * @param category Non-empty storage category requested for deletion.
     *
     * @author Dmitriy Moroz
     */
    data class RequestClearCategory(val category: UsageCategory) : StorageUsageIntent

    /**
     * Delete one category after the confirmation dialog is accepted.
     *
     * @param category Storage category confirmed for deletion.
     *
     * @author Dmitriy Moroz
     */
    data class ConfirmClearCategory(val category: UsageCategory) : StorageUsageIntent

    /**
     * Ask the UI to confirm deleting every non-empty storage category.
     *
     * @author Dmitriy Moroz
     */
    data object RequestClearAll : StorageUsageIntent

    /**
     * Delete the confirmed set of categories.
     *
     * @param categories Storage categories that the confirmation dialog listed for deletion.
     *
     * @author Dmitriy Moroz
     */
    data class ConfirmClearAll(val categories: List<UsageCategory>) : StorageUsageIntent

    /**
     * Close any visible storage confirmation dialog.
     *
     * @author Dmitriy Moroz
     */
    data object DismissDialog : StorageUsageIntent
}
