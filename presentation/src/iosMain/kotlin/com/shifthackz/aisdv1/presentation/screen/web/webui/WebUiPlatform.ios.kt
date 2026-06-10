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

@Composable
internal actual fun WebUiBackHandler(
    onBack: () -> Unit,
) = Unit

private class IosWebUiController : WebUiController {
    var webView: WKWebView? = null

    override val canGoBack: Boolean
        get() = webView?.canGoBack ?: false

    override fun goBack() {
        webView?.goBack()
    }
}

private fun String.toRequest(): NSURLRequest =
    NSURL.URLWithString(this)?.let(::NSURLRequest)
        ?: NSURLRequest(NSURL.URLWithString("about:blank")!!)
