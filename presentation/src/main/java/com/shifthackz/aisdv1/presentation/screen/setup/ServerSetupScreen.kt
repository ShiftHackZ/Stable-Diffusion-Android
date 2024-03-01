@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.ui.MviScreen2
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.setup.components.ConfigurationStepBar
import com.shifthackz.aisdv1.presentation.screen.setup.steps.ConfigurationStep
import com.shifthackz.aisdv1.presentation.screen.setup.steps.SourceSelectionStep
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ServerSetupScreen(
    private val viewModel: ServerSetupViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val onServerSetupComplete: () -> Unit = {},
    private val launchUrl: (String) -> Unit = {},
    private val launchManageStoragePermission: () -> Unit = {},
) : MviScreen2<ServerSetupState, ServerSetupIntent, ServerSetupEffect>(viewModel), KoinComponent {

    private val buildInfoProvider: BuildInfoProvider by inject()

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsStateWithLifecycle().value,
            buildInfoProvider = buildInfoProvider,
            handleIntent = viewModel::handleIntent,
        )
    }

    override fun processEffect(effect: ServerSetupEffect) = when (effect) {
        ServerSetupEffect.CompleteSetup -> onServerSetupComplete()
        ServerSetupEffect.LaunchManageStoragePermission -> launchManageStoragePermission()
        is ServerSetupEffect.LaunchUrl -> launchUrl(effect.url)
        ServerSetupEffect.NavigateBack -> onNavigateBack()
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    handleIntent: (ServerSetupIntent) -> Unit = {},
) {
    BackHandler(
        enabled = state.step != ServerSetupState.Step.SOURCE,
    ) {
        handleIntent(ServerSetupIntent.NavigateBack)
    }
    Box(modifier) {
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.title_server_setup),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        navigationIcon = {
                            if (state.showBackNavArrow || state.step.ordinal > 0) {
                                IconButton(
                                    onClick = {
                                        handleIntent(ServerSetupIntent.NavigateBack)
                                    },
                                    content = {
                                        Icon(
                                            Icons.AutoMirrored.Outlined.ArrowBack,
                                            contentDescription = "Back button",
                                        )
                                    },
                                )
                            }
                        },
                    )
                    ConfigurationStepBar(currentStep = state.step)
                }
            },
            bottomBar = {
                Button(
                    modifier = Modifier
                        .height(height = 68.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp, top = 8.dp),
                    onClick = { handleIntent(ServerSetupIntent.MainButtonClick) },
                    enabled = when (state.step) {
                        ServerSetupState.Step.CONFIGURE -> when (state.mode) {
                            ServerSource.LOCAL -> state.localModels.any {
                                it.downloaded && it.selected
                            }

                            else -> true
                        }

                        else -> true
                    },
                ) {
                    Text(
                        text = stringResource(
                            id = when (state.step) {
                                ServerSetupState.Step.SOURCE -> R.string.next
                                else -> when (state.mode) {
                                    ServerSource.LOCAL -> R.string.action_setup
                                    else -> R.string.action_connect
                                }
                            },
                        ),
                        color = LocalContentColor.current,
                    )
                }
            },
            content = { paddingValues ->
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { ServerSetupState.Step.entries.size },
                )

                HorizontalPager(
                    modifier = Modifier.padding(paddingValues),
                    state = pagerState,
                    userScrollEnabled = false,
                ) { index ->
                    when (ServerSetupState.Step.entries[index]) {
                        ServerSetupState.Step.SOURCE -> SourceSelectionStep(
                            state = state,
                            handleIntent = handleIntent,
                        )

                        ServerSetupState.Step.CONFIGURE -> ConfigurationStep(
                            state = state,
                            buildInfoProvider = buildInfoProvider,
                            handleIntent = handleIntent,
                        )
                    }
                }

                LaunchedEffect(state.step) {
                    pagerState.animateScrollToPage(state.step.ordinal)
                }
            },
        )
        when (state.screenDialog) {
            ServerSetupState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )

            is ServerSetupState.Dialog.Error -> ErrorDialog(
                text = state.screenDialog.error,
                onDismissRequest = { handleIntent(ServerSetupIntent.DismissDialog) },
            )

            ServerSetupState.Dialog.None -> Unit
        }
    }
}
