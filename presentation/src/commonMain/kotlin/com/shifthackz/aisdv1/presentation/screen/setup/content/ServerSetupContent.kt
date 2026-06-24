@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.component.ConfigurationStepBar
import com.shifthackz.aisdv1.presentation.screen.setup.component.ServerSetupLoading
import com.shifthackz.aisdv1.presentation.screen.setup.component.ServerSetupModal
import com.shifthackz.aisdv1.presentation.screen.setup.component.mainButtonEnabled
import com.shifthackz.aisdv1.presentation.screen.setup.form.local.ConfigurationStep
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.source.SourceFiltersBottomSheet
import com.shifthackz.aisdv1.presentation.screen.setup.source.SourceSelectionStep
import com.shifthackz.aisdv1.presentation.screen.setup.source.SourceSelectionToolbar
import com.shifthackz.aisdv1.presentation.screen.setup.source.SourceSortBottomSheet
import com.shifthackz.aisdv1.presentation.theme.global.persistentBottomBarWindowInsets
import com.shifthackz.aisdv1.presentation.theme.global.persistentTopAppBarWindowInsets

@Composable
fun ServerSetupContent(
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit,
    modifier: Modifier = Modifier,
    strings: ServerSetupStrings = ServerSetupStrings(),
) {
    val sourceListState = rememberLazyListState()
    val configurationListState = rememberLazyListState()

    LaunchedEffect(state.mode) {
        configurationListState.scrollToItem(0)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = strings.title,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        navigationIcon = {
                            if (state.showBackNavArrow || state.step != ServerSetupState.Step.SOURCE) {
                                IconButton(
                                    onClick = { processIntent(ServerSetupIntent.NavigateBack) },
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                        contentDescription = strings.backContentDescription,
                                    )
                                }
                            }
                        },
                        windowInsets = persistentTopAppBarWindowInsets(),
                    )
                    ConfigurationStepBar(
                        currentStep = state.step,
                        strings = strings,
                    )
                    if (state.step == ServerSetupState.Step.SOURCE && !state.loadingConfiguration) {
                        SourceSelectionToolbar(
                            state = state,
                            strings = strings,
                            processIntent = processIntent,
                        )
                    }
                }
            },
            bottomBar = {
                ServerSetupBottomBar(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )
            },
        ) { paddingValues ->
            if (state.loadingConfiguration) {
                ServerSetupLoading(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    text = strings.loadingConfiguration,
                )
            } else {
                when (state.step) {
                    ServerSetupState.Step.SOURCE -> SourceSelectionStep(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        state = state,
                        strings = strings,
                        listState = sourceListState,
                        processIntent = processIntent,
                    )

                    ServerSetupState.Step.CONFIGURE -> ConfigurationStep(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        state = state,
                        strings = strings,
                        listState = configurationListState,
                        processIntent = processIntent,
                    )
                }
            }
        }
        ServerSetupModal(
            modal = state.modal,
            strings = strings,
            processIntent = processIntent,
        )
        if (state.modal == ServerSetupState.Modal.SourceFilters) {
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { value -> value != SheetValue.PartiallyExpanded },
            )
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { processIntent(ServerSetupIntent.DismissDialog) },
                shape = RectangleShape,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                SourceFiltersBottomSheet(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )
            }
        }
        if (state.modal == ServerSetupState.Modal.SourceSort) {
            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
                confirmValueChange = { value -> value != SheetValue.PartiallyExpanded },
            )
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { processIntent(ServerSetupIntent.DismissDialog) },
                shape = RectangleShape,
                containerColor = MaterialTheme.colorScheme.background,
            ) {
                SourceSortBottomSheet(
                    state = state,
                    strings = strings,
                    processIntent = processIntent,
                )
            }
        }
    }
}

@Composable
private fun ServerSetupBottomBar(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    val isSdaiCloudTermsStep = state.step == ServerSetupState.Step.CONFIGURE &&
        state.mode == ServerSource.SDAI_CLOUD

    Column(
        modifier = Modifier
            .windowInsetsPadding(persistentBottomBarWindowInsets())
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (isSdaiCloudTermsStep) {
            SdaiCloudTermsConsentRow(
                state = state,
                strings = strings,
                processIntent = processIntent,
            )
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !state.loadingConfiguration && state.mainButtonEnabled,
            onClick = { processIntent(ServerSetupIntent.MainButtonClick) },
        ) {
            Text(
                text = state.mainButtonText(strings),
                color = LocalContentColor.current,
            )
        }
    }
}

@Composable
private fun SdaiCloudTermsConsentRow(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    val enabled = !state.sdaiCloudTermsLoading &&
        state.sdaiCloudTermsVersion.isNotBlank() &&
        !state.sdaiCloudTermsLoadFailed
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled) {
                    processIntent(ServerSetupIntent.UpdateSdaiCloudConsent(!state.sdaiCloudConsentAccepted))
                }
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = state.sdaiCloudConsentAccepted,
                enabled = enabled,
                onCheckedChange = { value ->
                    processIntent(ServerSetupIntent.UpdateSdaiCloudConsent(value))
                },
            )
            Text(
                modifier = Modifier.weight(1f),
                text = strings.sdaiCloudTermsAgree,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        if (state.sdaiCloudConsentValidationError != null && enabled) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = strings.sdaiCloudConsentRequired,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

private fun ServerSetupState.mainButtonText(strings: ServerSetupStrings): String =
    when (step) {
        ServerSetupState.Step.SOURCE -> strings.next
        ServerSetupState.Step.CONFIGURE -> when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX,
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
            ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
            -> strings.setup

            else -> strings.connect
        }
    }
