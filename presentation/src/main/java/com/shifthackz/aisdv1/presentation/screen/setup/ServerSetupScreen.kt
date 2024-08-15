@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.common.extensions.showToast
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.screen.setup.components.ConfigurationStepBar
import com.shifthackz.aisdv1.presentation.screen.setup.steps.ConfigurationStep
import com.shifthackz.aisdv1.presentation.screen.setup.steps.SourceSelectionStep
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ServerSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: ServerSetupViewModel,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val storagePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.any { !it }) context.showToast("Granted successfully")
    }

    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                ServerSetupEffect.LaunchManageStoragePermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        context.startActivity(intent)
                    } else {
                        if (PermissionUtil.checkStoragePermission(context, storagePermission::launch)) {
                            context.showToast("Already Granted")
                        }
                    }
                }
                is ServerSetupEffect.LaunchUrl -> context.openUrl(effect.url)
                ServerSetupEffect.HideKeyboard -> keyboardController?.hide()
            }
        },
    ) { state, intentHandler ->
        ServerSetupScreenContent(
            modifier = modifier.fillMaxSize(),
            state = state,
            buildInfoProvider = buildInfoProvider,
            processIntent = intentHandler,
        )
    }
}

@Composable
fun ServerSetupScreenContent(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    processIntent: (ServerSetupIntent) -> Unit = {},
) {
    BackHandler(
        enabled = state.step != ServerSetupState.Step.SOURCE,
    ) {
        processIntent(ServerSetupIntent.NavigateBack)
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
                                text = stringResource(id = LocalizationR.string.title_server_setup),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        navigationIcon = {
                            if (state.showBackNavArrow || state.step.ordinal > 0) {
                                IconButton(
                                    onClick = {
                                        processIntent(ServerSetupIntent.NavigateBack)
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
                        .testTag(ServerSetupScreenTags.MAIN_BUTTON)
                        .height(height = 68.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp, top = 8.dp),
                    onClick = { processIntent(ServerSetupIntent.MainButtonClick) },
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
                                ServerSetupState.Step.SOURCE -> LocalizationR.string.next
                                else -> when (state.mode) {
                                    ServerSource.LOCAL -> LocalizationR.string.action_setup
                                    else -> LocalizationR.string.action_connect
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
                            processIntent = processIntent,
                        )

                        ServerSetupState.Step.CONFIGURE -> ConfigurationStep(
                            state = state,
                            buildInfoProvider = buildInfoProvider,
                            processIntent = processIntent,
                        )
                    }
                }

                LaunchedEffect(state.step) {
                    pagerState.animateScrollToPage(state.step.ordinal)
                }
            },
        )
        ModalRenderer(screenModal = state.screenModal) {
            (it as? ServerSetupIntent)?.let(processIntent::invoke)
        }
    }
}
