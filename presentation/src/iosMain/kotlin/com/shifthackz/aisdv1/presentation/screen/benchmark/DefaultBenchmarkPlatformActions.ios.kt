package com.shifthackz.aisdv1.presentation.screen.benchmark

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

/**
 * Creates benchmark platform actions for iOS.
 *
 * @return iOS platform actions.
 * @author Dmitriy Moroz
 */
actual fun createDefaultBenchmarkPlatformActions(): BenchmarkPlatformActions =
    IosBenchmarkPlatformActions()

/**
 * iOS implementation of benchmark platform side effects.
 *
 * @author Dmitriy Moroz
 */
private class IosBenchmarkPlatformActions : BenchmarkPlatformActions {

    override suspend fun shareText(text: String): BenchmarkActionResult =
        suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                val presenter = UIApplication.sharedApplication
                    .rootViewController()
                    ?.topMostPresentedViewController()
                if (presenter == null) {
                    if (continuation.isActive) {
                        continuation.resume(
                            BenchmarkActionResult.Failed("Unable to open share sheet"),
                        )
                    }
                    return@dispatch_async
                }

                val controller = UIActivityViewController(
                    activityItems = listOf(text),
                    applicationActivities = null,
                )
                controller.modalPresentationStyle = UIModalPresentationFullScreen
                presenter.presentViewController(
                    viewControllerToPresent = controller,
                    animated = true,
                    completion = {
                        if (continuation.isActive) {
                            continuation.resume(BenchmarkActionResult.Done)
                        }
                    },
                )
            }
        }
}

private fun UIApplication.rootViewController(): UIViewController? =
    keyWindow?.rootViewController
        ?: windows
            .filterIsInstance<UIWindow>()
            .firstOrNull(UIWindow::isKeyWindow)
            ?.rootViewController
        ?: windows
            .filterIsInstance<UIWindow>()
            .firstOrNull()
            ?.rootViewController

private fun UIViewController.topMostPresentedViewController(): UIViewController =
    presentedViewController?.topMostPresentedViewController() ?: this
