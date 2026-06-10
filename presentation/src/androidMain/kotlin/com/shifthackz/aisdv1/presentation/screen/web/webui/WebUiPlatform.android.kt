package com.shifthackz.aisdv1.presentation.screen.web.webui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
internal actual fun WebUiBrowser(
    modifier: Modifier,
    url: String,
    onLoadingChanged: (Boolean) -> Unit,
    onControllerChanged: (WebUiController?) -> Unit,
) {
    val client: WebViewClient = remember {
        WebUiClient(
            onLoadingChanged = onLoadingChanged,
        )
    }
    AndroidWebUiView(
        modifier = modifier,
        url = url,
        client = client,
        onWebViewChanged = { webView ->
            onControllerChanged(AndroidWebUiController(webView))
        },
    )
}

@Composable
internal actual fun WebUiBackHandler(
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
}

private class AndroidWebUiController(
    private val webView: WebView,
) : WebUiController {
    override val canGoBack: Boolean
        get() = webView.canGoBack()

    override fun goBack() {
        webView.goBack()
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun AndroidWebUiView(
    modifier: Modifier = Modifier,
    url: String,
    client: WebViewClient,
    onWebViewChanged: (WebView) -> Unit = {},
) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.apply {
                    domStorageEnabled = true
                    javaScriptEnabled = true
                    setSupportZoom(true)
                }
                webViewClient = client
                loadUrl(url)
                onWebViewChanged(this)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
            onWebViewChanged(webView)
        },
    )
}

private class WebUiClient(
    private val onLoadingChanged: (Boolean) -> Unit = {},
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return false
    }

    @Deprecated("Deprecated in Java", ReplaceWith("false"))
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return false
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        onLoadingChanged(true)
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        onLoadingChanged(false)
        super.onPageFinished(view, url)
    }
}
