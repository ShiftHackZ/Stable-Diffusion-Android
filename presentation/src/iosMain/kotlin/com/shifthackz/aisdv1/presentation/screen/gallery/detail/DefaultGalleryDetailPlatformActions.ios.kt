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

/**
 * Creates the SDAI value produced by `createDefaultGalleryDetailPlatformActions`.
 *
 * @return Result produced by `createDefaultGalleryDetailPlatformActions`.
 * @author Dmitriy Moroz
 */
actual fun createDefaultGalleryDetailPlatformActions(): GalleryDetailPlatformActions =
    IosGalleryDetailPlatformActions()

/**
 * Coordinates `IosGalleryDetailPlatformActions` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class IosGalleryDetailPlatformActions : GalleryDetailPlatformActions {

    /**
     * Exposes the `imageSaver` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSaver = createPlatformImageSaver()
    /**
     * Exposes the `imageSharer` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val imageSharer = createPlatformImageSharer()

    /**
     * Performs the SDAI side effect handled by `saveImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `saveImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun saveImage(base64: String): GalleryDetailActionResult =
        when (val result = imageSaver.save(base64)) {
            ImageSaveResult.Saved -> GalleryDetailActionResult.Done
            ImageSaveResult.Unsupported -> GalleryDetailActionResult.Unsupported
            is ImageSaveResult.Failed -> GalleryDetailActionResult.Failed(result.message)
        }

    /**
     * Performs the SDAI side effect handled by `shareImage`.
     *
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `shareImage`.
     * @author Dmitriy Moroz
     */
    override suspend fun shareImage(base64: String): GalleryDetailActionResult =
        when (val result = imageSharer.share(base64)) {
            ImageShareResult.Sent -> GalleryDetailActionResult.Done
            ImageShareResult.Unsupported -> GalleryDetailActionResult.Unsupported
            is ImageShareResult.Failed -> GalleryDetailActionResult.Failed(result.message)
        }

    /**
     * Performs the SDAI side effect handled by `shareText`.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `shareText`.
     * @author Dmitriy Moroz
     */
    override suspend fun shareText(text: String): GalleryDetailActionResult =
        shareActivityItem(text)

    /**
     * Executes the `copyText` step in the SDAI presentation layer.
     *
     * @param text text value consumed by the API.
     * @return Result produced by `copyText`.
     * @author Dmitriy Moroz
     */
    override suspend fun copyText(text: String): GalleryDetailActionResult =
        suspendCancellableCoroutine { continuation ->
            dispatch_async(dispatch_get_main_queue()) {
                UIPasteboard.generalPasteboard.string = text
                if (continuation.isActive) {
                    continuation.resume(GalleryDetailActionResult.Done)
                }
            }
        }

    /**
     * Performs the SDAI side effect handled by `shareActivityItem`.
     *
     * @param item item value consumed by the API.
     * @return Result produced by `shareActivityItem`.
     * @author Dmitriy Moroz
     */
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

/**
 * Executes the `rootViewController` step in the SDAI presentation layer.
 *
 * @return Result produced by `rootViewController`.
 * @author Dmitriy Moroz
 */
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

/**
 * Converts SDAI data with `topMostPresentedViewController`.
 *
 * @return Result produced by `topMostPresentedViewController`.
 * @author Dmitriy Moroz
 */
private fun UIViewController.topMostPresentedViewController(): UIViewController =
    presentedViewController?.topMostPresentedViewController() ?: this
