package com.shifthackz.aisdv1.presentation.screen.web

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    modifier: Modifier = Modifier,
    url: String,
    client: WebViewClient? = null,
    webViewCallback: (WebView) -> Unit = {},
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
                    webViewClient = client ?: SdaiWebViewClient()
                }
                loadUrl(url)
                webViewCallback(this)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
            webViewCallback(webView)
        },
    )
}
