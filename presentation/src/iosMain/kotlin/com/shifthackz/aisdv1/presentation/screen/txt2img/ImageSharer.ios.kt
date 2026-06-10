package com.shifthackz.aisdv1.presentation.screen.txt2img

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

actual fun createPlatformImageSharer(): ImageSharer = IosImageSharer()

private class IosImageSharer : ImageSharer {

    override suspend fun share(base64: String): ImageShareResult {
        val image = runCatching { base64.decodeGeneratedUiImage() }.getOrElse { t ->
            return ImageShareResult.Failed(t.message ?: "Unable to decode generated image")
        }

        return suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                val presenter = UIApplication.sharedApplication
                    .rootViewController()
                    ?.topMostPresentedViewController()
                if (presenter == null) {
                    if (continuation.isActive) {
                        continuation.resume(ImageShareResult.Failed("Unable to open share sheet"))
                    }
                    return@dispatch_async
                }

                val controller = UIActivityViewController(
                    activityItems = listOf(image),
                    applicationActivities = null,
                )
                controller.modalPresentationStyle = UIModalPresentationFullScreen
                presenter.presentViewController(
                    viewControllerToPresent = controller,
                    animated = true,
                    completion = {
                        if (continuation.isActive) {
                            continuation.resume(ImageShareResult.Sent)
                        }
                    },
                )
            }
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
