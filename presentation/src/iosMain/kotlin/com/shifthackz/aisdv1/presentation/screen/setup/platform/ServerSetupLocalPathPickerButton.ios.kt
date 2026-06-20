package com.shifthackz.aisdv1.presentation.screen.setup.platform

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.domain.entity.ServerSource
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIModalPresentationFullScreen
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject

@Composable
internal actual fun ServerSetupLocalPathPickerButton(
    modifier: Modifier,
    text: String,
    onPathSelected: (String) -> Unit,
) {
    val picker = remember { IosLocalPathPicker() }
    OutlinedButton(
        modifier = modifier,
        onClick = { picker.present(onPathSelected) },
    ) {
        Text(text = text)
    }
}

internal actual fun isLocalGenerationSetupAvailable(): Boolean = true

internal actual fun isServerSourceAvailableOnPlatform(source: ServerSource): Boolean = when (source) {
    ServerSource.LOCAL_APPLE_CORE_ML,
    -> isAppleLocalRuntimeAvailable()

    ServerSource.LOCAL_APPLE_BONSAI,
    -> isAppleBonsaiRuntimeAvailable()

    else -> true
}

private fun isAppleLocalRuntimeAvailable(): Boolean =
    UIDevice.currentDevice.systemVersion
        .split(".")
        .mapNotNull(String::toIntOrNull)
        .let { parts ->
            val major = parts.getOrElse(0) { 0 }
            val minor = parts.getOrElse(1) { 0 }
            major > 16 || major == 16 && minor >= 2
        }

private fun isAppleBonsaiRuntimeAvailable(): Boolean =
    UIDevice.currentDevice.systemVersion
        .split(".")
        .mapNotNull(String::toIntOrNull)
        .let { parts ->
            val major = parts.getOrElse(0) { 0 }
            major >= 17
        }

private class IosLocalPathPicker : NSObject(), UIDocumentPickerDelegateProtocol {

    private var onPathSelected: ((String) -> Unit)? = null
    private val retainedSecurityScopedUrls = mutableListOf<NSURL>()

    fun present(onPathSelected: (String) -> Unit) {
        val presenter = UIApplication.sharedApplication
            .rootViewController()
            ?.topMostPresentedViewController()
            ?: return
        this.onPathSelected = onPathSelected
        val controller = UIDocumentPickerViewController(
            forOpeningContentTypes = listOf(UTTypeFolder),
            asCopy = false,
        )
        controller.delegate = this
        controller.allowsMultipleSelection = false
        controller.modalPresentationStyle = UIModalPresentationFullScreen
        presenter.presentViewController(
            viewControllerToPresent = controller,
            animated = true,
            completion = null,
        )
    }

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL ?: return
        if (url.startAccessingSecurityScopedResource()) {
            retainedSecurityScopedUrls.add(url)
        }
        url.path?.let { path -> onPathSelected?.invoke(path) }
        onPathSelected = null
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        onPathSelected = null
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
