@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.web.webui

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
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.WebUiRouter
import com.shifthackz.aisdv1.presentation.widget.source.getName
import org.koin.core.parameter.parametersOf

@Composable
fun WebUiScreen(
    router: WebUiRouter? = null,
) {
    val koin = remember { initKoin() }
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<WebUiRouter>()
    }
    val viewModel = remember(koin, resolvedRouter) {
        koin.get<WebUiViewModel> {
            parametersOf(resolvedRouter)
        }
    }
    MviComponent(
        viewModel = viewModel,
    ) { state, intentHandler ->
        WebUiScreenContent(
            state = state,
            processIntent = intentHandler,
        )
    }
}

@Composable
fun WebUiScreenContent(
    state: WebUiState = WebUiState(),
    processIntent: (WebUiIntent) -> Unit = {},
) {
    var pageLoading by remember { mutableStateOf(true) }
    var webController: WebUiController? by remember { mutableStateOf(null) }

    fun navigateBack() {
        if (state.loading || state.source == ServerSource.SWARM_UI) {
            processIntent(WebUiIntent.NavigateBack)
            return
        }
        val controller = webController
        if (controller?.canGoBack == true) {
            controller.goBack()
        } else {
            processIntent(WebUiIntent.NavigateBack)
        }
    }

    WebUiBackHandler {
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
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Localization.string("action_back"),
                        )
                    }
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
                        WebUiBrowser(
                            modifier = Modifier.fillMaxSize(),
                            url = url,
                            onLoadingChanged = { pageLoading = it },
                            onControllerChanged = { webController = it },
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

interface WebUiController {
    val canGoBack: Boolean
    fun goBack()
}

@Composable
internal expect fun WebUiBrowser(
    modifier: Modifier = Modifier,
    url: String,
    onLoadingChanged: (Boolean) -> Unit = {},
    onControllerChanged: (WebUiController?) -> Unit = {},
)

@Composable
internal expect fun WebUiBackHandler(
    onBack: () -> Unit,
)
