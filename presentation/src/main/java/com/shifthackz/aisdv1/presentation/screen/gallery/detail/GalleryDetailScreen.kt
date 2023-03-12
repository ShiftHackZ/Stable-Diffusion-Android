@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImageSource

import java.io.File

class GalleryDetailScreen(
    private val viewModel: GalleryDetailViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val shareGalleryFile: (File) -> Unit = {},
    private val shareGenerationParams: (GalleryDetailState) -> Unit = {},
) : MviScreen<GalleryDetailState, GalleryDetailEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onNavigateBack = onNavigateBack,
            onTabSelected = viewModel::selectTab,
            onExportImageToolbarClick = viewModel::share,
            onExportParamsClick = shareGenerationParams,
            onDeleteButtonClick = viewModel::showDeleteConfirmDialog,
            onDeleteConfirmClick = viewModel::delete,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
        )
    }

    override fun processEffect(effect: GalleryDetailEffect) = when (effect) {
        is GalleryDetailEffect.ShareImageFile -> shareGalleryFile(effect.file)
        GalleryDetailEffect.NavigateBack -> onNavigateBack()
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryDetailState,
    onNavigateBack: () -> Unit = {},
    onTabSelected: (GalleryDetailState.Tab) -> Unit = {},
    onExportImageToolbarClick: () -> Unit = {},
    onExportParamsClick: (GalleryDetailState.Content) -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    onDeleteConfirmClick: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(id = R.string.title_gallery_details))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            content = {
                                Icon(
                                    Icons.Outlined.ArrowBack,
                                    contentDescription = "Back button",
                                )
                            },
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = onExportImageToolbarClick,
                            content = {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_share),
                                    contentDescription = "Export",
                                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                                )
                            },
                        )
                    }
                )
            },
            content = { paddingValues ->
                val contentModifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

                when (state) {
                    is GalleryDetailState.Content -> GalleryDetailContentState(
                        modifier = contentModifier,
                        state = state,
                        onDeleteButtonClick = onDeleteButtonClick,
                        onExportParamsClick = onExportParamsClick,
                    )
                    is GalleryDetailState.Loading -> Unit
                }
            },
            bottomBar = { GalleryDetailNavigationBar(state, onTabSelected) },
        )
        when (state.screenDialog) {
            GalleryDetailState.Dialog.DeleteConfirm -> DecisionInteractiveDialog(
                title = R.string.interaction_delete_generation_title.asUiText(),
                text = R.string.interaction_delete_generation_sub_title.asUiText(),
                confirmActionResId = R.string.yes,
                dismissActionResId = R.string.no,
                onConfirmAction = onDeleteConfirmClick,
                onDismissRequest = onDismissScreenDialog,
            )
            GalleryDetailState.Dialog.None -> Unit
        }
    }
}

@Composable
private fun GalleryDetailNavigationBar(
    state: GalleryDetailState,
    onTabSelected: (GalleryDetailState.Tab) -> Unit,
) {
    NavigationBar {
        state.tabs.forEach { tab ->
            NavigationBarItem(
                selected = state.selectedTab == tab,
                label = {
                    Text(stringResource(id = tab.label))
                },
                icon = {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(tab.iconRes),
                        contentDescription = stringResource(id = R.string.gallery_tab_image),
                        colorFilter = ColorFilter.tint(LocalContentColor.current),
                    )
                },
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun GalleryDetailContentState(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
    onDeleteButtonClick: () -> Unit = {},
    onExportParamsClick: (GalleryDetailState.Content) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        when (state.selectedTab) {
            GalleryDetailState.Tab.IMAGE -> ZoomableImage(
                modifier = Modifier.fillMaxSize(),
                source = ZoomableImageSource.Bmp(state.bitmap),
            )
            GalleryDetailState.Tab.ORIGINAL -> state.inputBitmap?.let { bmp ->
                ZoomableImage(
                    modifier = Modifier.fillMaxSize(),
                    source = ZoomableImageSource.Bmp(bmp),
                )
            }
            GalleryDetailState.Tab.INFO -> GalleryDetailsTable(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onDeleteButtonClick = onDeleteButtonClick,
                onExportParamsClick = onExportParamsClick,
            )
        }
    }
}

@Composable
private fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
    onDeleteButtonClick: () -> Unit = {},
    onExportParamsClick: (GalleryDetailState.Content) -> Unit = {},
) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(paddingValues),
            ) {
                val colorOddBg = MaterialTheme.colorScheme.secondaryContainer
                val colorOddText = MaterialTheme.colorScheme.onSecondaryContainer
                val colorEvenBg = MaterialTheme.colorScheme.tertiaryContainer
                val colorEvenText = MaterialTheme.colorScheme.onTertiaryContainer
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_date.asUiText(),
                    value = state.createdAt,
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_type.asUiText(),
                    value = state.type,
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_prompt.asUiText(),
                    value = state.prompt,
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_negative_prompt.asUiText(),
                    value = state.negativePrompt,
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_size.asUiText(),
                    value = state.size,
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_sampling_steps.asUiText(),
                    value = state.samplingSteps,
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_cfg.asUiText(),
                    value = state.cfgScale,
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_restore_faces.asUiText(),
                    value = state.restoreFaces,
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_sampler.asUiText(),
                    value = state.sampler,
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_seed.asUiText(),
                    value = state.seed,
                    color = colorEvenText,
                )
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 2.dp),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onExportParamsClick(state) },
                ) {
                    Text(
                        text = stringResource(id = R.string.action_share_prompt),
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onDeleteButtonClick,
                ) {
                    Text(
                        text = stringResource(id = R.string.action_delete_image),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
    )
}

@Composable
private fun GalleryDetailRow(
    modifier: Modifier = Modifier,
    column1Weight: Float = 0.3f,
    column2Weight: Float = 0.7f,
    name: UiText,
    value: UiText,
    color: Color,
) {
    Row(modifier) {
        GalleryDetailCell(
            text = name,
            modifier = Modifier.weight(column1Weight),
            color = color,
        )
        GalleryDetailCell(
            text = value,
            modifier = Modifier.weight(column2Weight),
            color = color,
        )
    }
}

@Composable
private fun GalleryDetailCell(
    modifier: Modifier = Modifier,
    text: UiText,
    color: Color,
) {
    Text(
        text = text.asString(),
        modifier = modifier.padding(8.dp),
        color = color,
    )
}
