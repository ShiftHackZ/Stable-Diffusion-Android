package com.shifthackz.aisdv1.presentation.screen.web.webui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.WebKit.WKWebView

/**
 * Renders the `WebUiBrowser` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param url remote URL used by the operation.
 * @param onLoadingChanged callback invoked by the component.
 * @param onControllerChanged callback invoked by the component.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun WebUiBrowser(
    modifier: Modifier,
    url: String,
    onLoadingChanged: (Boolean) -> Unit,
    onControllerChanged: (WebUiController?) -> Unit,
) {
    val controller = remember { IosWebUiController() }
    LaunchedEffect(url) {
        onLoadingChanged(true)
    }
    UIKitView(
        modifier = modifier,
        factory = {
            WKWebView().also { webView ->
                controller.webView = webView
                onControllerChanged(controller)
                webView.loadRequest(url.toRequest())
                onLoadingChanged(false)
            }
        },
        update = { webView ->
            controller.webView = webView
            onControllerChanged(controller)
            webView.loadRequest(url.toRequest())
            onLoadingChanged(false)
        },
    )
}

/**
 * Renders the `WebUiBackHandler` UI for the SDAI presentation layer.
 *
 * @param onBack callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun WebUiBackHandler(
    onBack: () -> Unit,
) = Unit

/**
 * Coordinates `IosWebUiController` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class IosWebUiController : WebUiController {
    /**
     * Exposes the `webView` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    var webView: WKWebView? = null

    /**
     * Exposes the `canGoBack` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val canGoBack: Boolean
        get() = webView?.canGoBack ?: false

    /**
     * Executes the `goBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun goBack() {
        webView?.goBack()
    }
}

/**
 * Converts SDAI data with `toRequest`.
 *
 * @return Result produced by `toRequest`.
 * @author Dmitriy Moroz
 */
private fun String.toRequest(): NSURLRequest =
    NSURL.URLWithString(this)?.let(::NSURLRequest)
        ?: NSURLRequest(NSURL.URLWithString("about:blank")!!)
