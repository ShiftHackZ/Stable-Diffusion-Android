package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaveResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageShareResult
import com.shifthackz.aisdv1.presentation.screen.txt2img.createPlatformImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.createPlatformImageSharer
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIPasteboard
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import kotlin.coroutines.resume

actual fun createDefaultGalleryDetailPlatformActions(): GalleryDetailPlatformActions =
    IosGalleryDetailPlatformActions()

private class IosGalleryDetailPlatformActions : GalleryDetailPlatformActions {

    private val imageSaver = createPlatformImageSaver()
    private val imageSharer = createPlatformImageSharer()

    override suspend fun saveImage(base64: String): GalleryDetailActionResult =
        when (val result = imageSaver.save(base64)) {
            ImageSaveResult.Saved -> GalleryDetailActionResult.Done
            ImageSaveResult.Unsupported -> GalleryDetailActionResult.Unsupported
            is ImageSaveResult.Failed -> GalleryDetailActionResult.Failed(result.message)
        }

    override suspend fun shareImage(base64: String): GalleryDetailActionResult =
        when (val result = imageSharer.share(base64)) {
            ImageShareResult.Sent -> GalleryDetailActionResult.Done
            ImageShareResult.Unsupported -> GalleryDetailActionResult.Unsupported
            is ImageShareResult.Failed -> GalleryDetailActionResult.Failed(result.message)
        }

    override suspend fun shareText(text: String): GalleryDetailActionResult =
        shareActivityItem(text)

    override suspend fun copyText(text: String): GalleryDetailActionResult =
        suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                UIPasteboard.generalPasteboard.string = text
                if (continuation.isActive) {
                    continuation.resume(GalleryDetailActionResult.Done)
                }
            }
        }

    private suspend fun shareActivityItem(item: Any): GalleryDetailActionResult =
        suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                val presenter = UIApplication.sharedApplication
                    .rootViewController()
                    ?.topMostPresentedViewController()
                if (presenter == null) {
                    if (continuation.isActive) {
                        continuation.resume(
                            GalleryDetailActionResult.Failed("Unable to open share sheet"),
                        )
                    }
                    return@dispatch_async
                }

                val controller = UIActivityViewController(
                    activityItems = listOf(item),
                    applicationActivities = null,
                )
                controller.modalPresentationStyle = UIModalPresentationFullScreen
                presenter.presentViewController(
                    viewControllerToPresent = controller,
                    animated = true,
                    completion = {
                        if (continuation.isActive) {
                            continuation.resume(GalleryDetailActionResult.Done)
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
