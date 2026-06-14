package com.shifthackz.aisdv1.presentation.screen.benchmark

import android.content.Context
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.sharing.shareText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

/**
 * Android implementation of benchmark platform side effects.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidBenchmarkPlatformActions(
    private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
) : BenchmarkPlatformActions {

    override suspend fun shareText(text: String): BenchmarkActionResult =
        try {
            withContext(dispatchersProvider.immediate) {
                context.shareText(text)
            }
            BenchmarkActionResult.Done
        } catch (e: CancellationException) {
            throw e
        } catch (t: Throwable) {
            BenchmarkActionResult.Failed(t.message ?: "Unable to share benchmark result")
        }
}
