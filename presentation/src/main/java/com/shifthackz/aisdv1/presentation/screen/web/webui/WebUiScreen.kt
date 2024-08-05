@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.web.webui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.web.SdaiWebViewClient
import com.shifthackz.aisdv1.presentation.screen.web.WebViewScreen
import com.shifthackz.aisdv1.presentation.widget.source.getName
import org.koin.androidx.compose.koinViewModel

@Composable
fun WebUiScreen() {
    MviComponent(
        viewModel = koinViewModel<WebUiViewModel>(),
        applySystemUiColors = false,
    ) { state, intentHandler ->
        WebUiScreenContent(state, intentHandler)
    }    
}

@Composable
private fun WebUiScreenContent(
    state: WebUiState = WebUiState(),
    processIntent: (WebUiIntent) -> Unit = {},
) {
    var pageLoading by remember { mutableStateOf(true) }
    var webView: WebView? by remember { mutableStateOf(null) }
    val client: WebViewClient = remember {
        SdaiWebViewClient(
            onLoadingChanged = { pageLoading = it },
        )
    }
    fun navigateBack() {
        if (state.loading || state.source == ServerSource.SWARM_UI) {
            processIntent(WebUiIntent.NavigateBack)
            return
        }
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            processIntent(WebUiIntent.NavigateBack)
        }
    }
    BackHandler {
        navigateBack()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = state.source.takeIf { !state.loading }?.getName() ?: "",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navigateBack() },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back button",
                            )
                        },
                    )
                },
            )
        },
        contentColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            targetState = !state.loading,
            label = "web_ui_root_state_animation",
        ) { contentVisible ->
            if (contentVisible) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    verticalArrangement = Arrangement.Top,
                ) {
                    AnimatedVisibility(visible = pageLoading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = AlertDialogDefaults.iconContentColor,
                        )
                    }
                    state.url.takeIf(String::isNotBlank)?.let { url ->
                        WebViewScreen(
                            url = url,
                            webViewCallback = { webView = it},
                            client = client,
                        )
                    }
                }
            } else {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f),
                    )
                }
            }
        }
    }
}
