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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.ZoomableImageSource
import java.io.File

class GalleryDetailScreen(
    private val viewModel: GalleryDetailViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val shareGalleryFile: (File) -> Unit = {},
) : MviScreen<GalleryDetailState, GalleryDetailEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onNavigateBack = onNavigateBack,
            onTabSelected = viewModel::selectTab,
            onExportToolbarClick = viewModel::share,
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
    onExportToolbarClick: () -> Unit = {},
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
                            onClick = onExportToolbarClick,
                            content = {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_share),
                                    contentDescription = "Export"
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
                    )
                    is GalleryDetailState.Loading -> Text("Load")
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
            )
        }
    }
}

@Composable
private fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
    onDeleteButtonClick: () -> Unit = {},
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
                val colorOdd = Color(0xFFefedf5)
                val colorEven = Color(0xFFe6def5)
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOdd),
                    name = R.string.gallery_info_field_date.asUiText(),
                    value = state.createdAt,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEven),
                    name = R.string.gallery_info_field_type.asUiText(),
                    value = state.type,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOdd),
                    name = R.string.gallery_info_field_prompt.asUiText(),
                    value = state.prompt,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEven),
                    name = R.string.gallery_info_field_negative_prompt.asUiText(),
                    value = state.negativePrompt,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOdd),
                    name = R.string.gallery_info_field_size.asUiText(),
                    value = state.size,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEven),
                    name = R.string.gallery_info_field_sampling_steps.asUiText(),
                    value = state.samplingSteps,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOdd),
                    name = R.string.gallery_info_field_cfg.asUiText(),
                    value = state.cfgScale,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEven),
                    name = R.string.gallery_info_field_restore_faces.asUiText(),
                    value = state.restoreFaces,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOdd),
                    name = R.string.gallery_info_field_sampler.asUiText(),
                    value = state.sampler,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEven),
                    name = R.string.gallery_info_field_seed.asUiText(),
                    value = state.seed,
                )
            }
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 16.dp),
                onClick = onDeleteButtonClick,
            ) {
                Text(
                    text = stringResource(id = R.string.action_delete_image)
                )
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
) {
    Row(modifier) {
        GalleryDetailCell(
            text = name,
            modifier = Modifier.weight(column1Weight)
        )
        GalleryDetailCell(
            text = value,
            modifier = Modifier.weight(column2Weight)
        )
    }
}

@Composable
private fun GalleryDetailCell(
    modifier: Modifier = Modifier,
    text: UiText,
) {
    Text(
        text = text.asString(),
        modifier = modifier
            .padding(8.dp)
    )
}
