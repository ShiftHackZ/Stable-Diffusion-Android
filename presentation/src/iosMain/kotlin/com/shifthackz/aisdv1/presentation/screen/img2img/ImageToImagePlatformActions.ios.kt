package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Renders the `rememberImageToImagePlatformActions` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberImageToImagePlatformActions`.
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberImageToImagePlatformActions(): ImageToImagePlatformActions {
    val picker = remember { IosImagePicker() }
    return remember(picker) {
        object : ImageToImagePlatformActions {
            override suspend fun pickImage(source: ImageToImagePickSource): ImageToImagePickResult =
                when (val outcome = picker.pick(source)) {
                    IosImagePickOutcome.Cancelled -> ImageToImagePickResult.Cancelled
                    is IosImagePickOutcome.Failed -> ImageToImagePickResult.Failed(outcome.message)
                    IosImagePickOutcome.Unsupported -> ImageToImagePickResult.Unsupported
                    is IosImagePickOutcome.Selected -> withContext(Dispatchers.Default) {
                        outcome.image
                            .base64Jpeg()
                            ?.let { cropBase64ImageToSquare(it) }
                            ?.let(ImageToImagePickResult::Selected)
                            ?: ImageToImagePickResult.Failed("Unable to read selected image")
                    }
                }
        }
    }
}

/**
 * Defines the `IosImagePickOutcome` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private sealed interface IosImagePickOutcome {
    /**
     * Carries `Selected` data through the SDAI presentation layer.
     *
     * @param image image value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Selected(val image: UIImage) : IosImagePickOutcome
    /**
     * Provides the `Cancelled` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Cancelled : IosImagePickOutcome
    /**
     * Provides the `Unsupported` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Unsupported : IosImagePickOutcome
    /**
     * Carries `Failed` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Failed(val message: String) : IosImagePickOutcome
}

/**
 * Coordinates `IosImagePicker` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class IosImagePicker : NSObject(),
    UIImagePickerControllerDelegateProtocol,
    UINavigationControllerDelegateProtocol {

    /**
     * Exposes the `continuation` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private var continuation: CancellableContinuation<IosImagePickOutcome>? = null

    /**
     * Executes the `pick` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @return Result produced by `pick`.
     * @author Dmitriy Moroz
     */
    suspend fun pick(source: ImageToImagePickSource): IosImagePickOutcome =
        suspendCancellableCoroutine { nextContinuation ->
            if (continuation != null) {
                nextContinuation.resume(
                    IosImagePickOutcome.Failed("Another image picker is already active"),
                )
                return@suspendCancellableCoroutine
            }
            continuation = nextContinuation
            nextContinuation.invokeOnCancellation {
                if (continuation == nextContinuation) {
                    continuation = null
                }
            }
            dispatch_async(dispatch_get_main_queue()) {
                present(source)
            }
        }

    /**
     * Executes the `present` step in the SDAI presentation layer.
     *
     * @param source source value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun present(source: ImageToImagePickSource) {
        val sourceType = when (source) {
            ImageToImagePickSource.Camera -> UI_IMAGE_PICKER_SOURCE_TYPE_CAMERA
            ImageToImagePickSource.Gallery -> UI_IMAGE_PICKER_SOURCE_TYPE_PHOTO_LIBRARY
        }
        if (!UIImagePickerController.isSourceTypeAvailable(sourceType)) {
            resume(IosImagePickOutcome.Unsupported)
            return
        }

        val presenter = UIApplication.sharedApplication
            .rootViewController()
            ?.topMostPresentedViewController()
        if (presenter == null) {
            resume(IosImagePickOutcome.Failed("Unable to open image picker"))
            return
        }

        val controller = UIImagePickerController()
        controller.sourceType = sourceType
        controller.delegate = this
        controller.modalPresentationStyle = UIModalPresentationFullScreen
        presenter.presentViewController(
            viewControllerToPresent = controller,
            animated = true,
            completion = null,
        )
    }

    /**
     * Executes the `imagePickerController` step in the SDAI presentation layer.
     *
     * @param picker picker value consumed by the API.
     * @param didFinishPickingMediaWithInfo did finish picking media with info value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val result = image
            ?.let(IosImagePickOutcome::Selected)
            ?: IosImagePickOutcome.Failed("Unable to read selected image")
        picker.dismissViewControllerAnimated(true, completion = null)
        resume(result)
    }

    /**
     * Executes the `imagePickerControllerDidCancel` step in the SDAI presentation layer.
     *
     * @param picker picker value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        resume(IosImagePickOutcome.Cancelled)
    }

    /**
     * Executes the `resume` step in the SDAI presentation layer.
     *
     * @param result result value consumed by the API.
     * @author Dmitriy Moroz
     */
    private fun resume(result: IosImagePickOutcome) {
        val current = continuation ?: return
        continuation = null
        current.resume(result)
    }
}

/**
 * Executes the `base64Jpeg` step in the SDAI presentation layer.
 *
 * @return Result produced by `base64Jpeg`.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalEncodingApi::class, ExperimentalForeignApi::class)
private fun UIImage.base64Jpeg(): String? {
    val data = UIImageJPEGRepresentation(this, compressionQuality = 0.95) ?: return null
    val length = data.length.toInt()
    if (length <= 0) return null
    val bytes = ByteArray(length)
    bytes.usePinned { pinned ->
        memcpy(pinned.addressOf(0), data.bytes, data.length)
    }
    return Base64.encode(bytes)
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

/**
 * Exposes the `UI_IMAGE_PICKER_SOURCE_TYPE_PHOTO_LIBRARY` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private val UI_IMAGE_PICKER_SOURCE_TYPE_PHOTO_LIBRARY =
    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
/**
 * Exposes the `UI_IMAGE_PICKER_SOURCE_TYPE_CAMERA` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private val UI_IMAGE_PICKER_SOURCE_TYPE_CAMERA =
    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
