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

/**
 * Renders the `WebUiBrowser` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param url remote URL used by the operation.
 * @param onLoadingChanged callback invoked by the component.
 * @param onControllerChanged callback invoked by the component.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `WebUiBackHandler` UI for the SDAI presentation layer.
 *
 * @param onBack callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal actual fun WebUiBackHandler(
    onBack: () -> Unit,
) {
    BackHandler(onBack = onBack)
}

/**
 * Coordinates `AndroidWebUiController` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class AndroidWebUiController(
    /**
     * Exposes the `webView` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val webView: WebView,
) : WebUiController {
    /**
     * Exposes the `canGoBack` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val canGoBack: Boolean
        get() = webView.canGoBack()

    /**
     * Executes the `goBack` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun goBack() {
        webView.goBack()
    }
}

/**
 * Renders the `AndroidWebUiView` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param url remote URL used by the operation.
 * @param client client value consumed by the API.
 * @param onWebViewChanged callback invoked by the component.
 * @author Dmitriy Moroz
 */
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

/**
 * Coordinates `WebUiClient` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class WebUiClient(
    /**
     * Exposes the `onLoadingChanged` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
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
