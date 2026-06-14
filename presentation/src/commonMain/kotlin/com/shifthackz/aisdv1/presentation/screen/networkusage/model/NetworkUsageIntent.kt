package com.shifthackz.aisdv1.presentation.screen.networkusage.model

import com.shifthackz.aisdv1.core.mvi.MviIntent
import com.shifthackz.aisdv1.presentation.model.UsageCategory

/**
 * User actions supported by the standalone network usage screen.
 *
 * @author Dmitriy Moroz
 */
sealed interface NetworkUsageIntent : MviIntent {
    /**
     * Close the screen and return to Settings.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : NetworkUsageIntent

    /**
     * Highlight a traffic category in the donut chart and center label.
     *
     * @param category Traffic category selected by tapping the row or donut segment.
     *
     * @author Dmitriy Moroz
     */
    data class SelectCategory(val category: UsageCategory) : NetworkUsageIntent

    /**
     * Reset all persisted traffic counters.
     *
     * @author Dmitriy Moroz
     */
    data object ResetStatistics : NetworkUsageIntent
}
