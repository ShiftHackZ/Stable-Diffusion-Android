package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
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
import kotlinx.coroutines.CancellableContinuation

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

private sealed interface IosImagePickOutcome {
    data class Selected(val image: UIImage) : IosImagePickOutcome
    data object Cancelled : IosImagePickOutcome
    data object Unsupported : IosImagePickOutcome
    data class Failed(val message: String) : IosImagePickOutcome
}

private class IosImagePicker : NSObject(),
    UIImagePickerControllerDelegateProtocol,
    UINavigationControllerDelegateProtocol {

    private var continuation: CancellableContinuation<IosImagePickOutcome>? = null

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

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        resume(IosImagePickOutcome.Cancelled)
    }

    private fun resume(result: IosImagePickOutcome) {
        val current = continuation ?: return
        continuation = null
        current.resume(result)
    }
}

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

private val UI_IMAGE_PICKER_SOURCE_TYPE_PHOTO_LIBRARY =
    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
private val UI_IMAGE_PICKER_SOURCE_TYPE_CAMERA =
    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
