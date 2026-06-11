package com.shifthackz.aisdv1.work

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.UIBackgroundTaskInvalid

/**
 * Wraps iOS finite background execution time for a generation job.
 *
 * @author Dmitriy Moroz
 */
internal class IosBackgroundExecution(
    private val name: String,
    private val activeJob: () -> Job?,
) {

    private var identifier: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid

    /**
     * Executes the `begin` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    fun begin() {
        if (identifier != UIBackgroundTaskInvalid) return
        identifier = UIApplication.sharedApplication.beginBackgroundTaskWithName(name) {
            activeJob()?.cancel(CancellationException("iOS background execution time expired."))
            end()
        }
    }

    /**
     * Executes the `end` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    fun end() {
        val current = identifier
        if (current == UIBackgroundTaskInvalid) return
        identifier = UIBackgroundTaskInvalid
        UIApplication.sharedApplication.endBackgroundTask(current)
    }
}
