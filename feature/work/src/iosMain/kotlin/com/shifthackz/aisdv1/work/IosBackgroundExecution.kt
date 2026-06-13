package com.shifthackz.aisdv1.work

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import platform.Foundation.NSThread
import platform.UIKit.UIApplication
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UIKit.UIBackgroundTaskInvalid
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_sync

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
        runOnMainThread {
            if (identifier != UIBackgroundTaskInvalid) return@runOnMainThread
            identifier = UIApplication.sharedApplication.beginBackgroundTaskWithName(name) {
                activeJob()?.cancel(CancellationException("iOS background execution time expired."))
                end()
            }
        }
    }

    /**
     * Executes the `end` step in the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    fun end() {
        runOnMainThread {
            val current = identifier
            if (current == UIBackgroundTaskInvalid) return@runOnMainThread
            identifier = UIBackgroundTaskInvalid
            UIApplication.sharedApplication.endBackgroundTask(current)
        }
    }

    private inline fun runOnMainThread(crossinline block: () -> Unit) {
        if (NSThread.isMainThread) {
            block()
        } else {
            dispatch_sync(dispatch_get_main_queue()) {
                block()
            }
        }
    }
}
