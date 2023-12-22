@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImageSource
import org.koin.core.component.KoinComponent
import java.io.File

class GalleryDetailScreen(
    private val viewModel: GalleryDetailViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val shareGalleryFile: (File) -> Unit = {},
    private val shareGenerationParams: (GalleryDetailState) -> Unit = {},
    private val copyToClipboard: (CharSequence) -> Unit = {},
) : MviScreen<GalleryDetailState, GalleryDetailEffect>(viewModel), KoinComponent {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onNavigateBack = onNavigateBack,
            onTabSelected = viewModel::selectTab,
            onCopyTextClick = copyToClipboard,
            onSendToTxt2Img = viewModel::sendPromptToTxt2Img,
            onSendToImg2Img = viewModel::sendPromptToImg2Img,
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
    onCopyTextClick: (CharSequence) -> Unit = {},
    onSendToTxt2Img: () -> Unit = {},
    onSendToImg2Img: () -> Unit = {},
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
                        onSendToTxt2Img = onSendToTxt2Img,
                        onSendToImg2Img = onSendToImg2Img,
                        onDeleteButtonClick = onDeleteButtonClick,
                        onExportParamsClick = onExportParamsClick,
                        onCopyTextClick = onCopyTextClick,
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
    Column {
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

}

@Composable
private fun GalleryDetailContentState(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
    onSendToTxt2Img: () -> Unit = {},
    onSendToImg2Img: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    onExportParamsClick: (GalleryDetailState.Content) -> Unit = {},
    onCopyTextClick: (CharSequence) -> Unit = {},
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
                onSendToTxt2Img = onSendToTxt2Img,
                onSendToImg2Img = onSendToImg2Img,
                onDeleteButtonClick = onDeleteButtonClick,
                onExportParamsClick = onExportParamsClick,
                onCopyTextClick = onCopyTextClick,
            )
        }
    }
}

@Composable
private fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
    onSendToTxt2Img: () -> Unit = {},
    onSendToImg2Img: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    onExportParamsClick: (GalleryDetailState.Content) -> Unit = {},
    onCopyTextClick: (CharSequence) -> Unit = {},
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
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_type.asUiText(),
                    value = state.type,
                    color = colorEvenText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_prompt.asUiText(),
                    value = state.prompt,
                    color = colorOddText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_negative_prompt.asUiText(),
                    value = state.negativePrompt,
                    color = colorEvenText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_size.asUiText(),
                    value = state.size,
                    color = colorOddText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_sampling_steps.asUiText(),
                    value = state.samplingSteps,
                    color = colorEvenText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_cfg.asUiText(),
                    value = state.cfgScale,
                    color = colorOddText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_restore_faces.asUiText(),
                    value = state.restoreFaces,
                    color = colorEvenText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_sampler.asUiText(),
                    value = state.sampler,
                    color = colorOddText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_seed.asUiText(),
                    value = state.seed,
                    color = colorEvenText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_sub_seed.asUiText(),
                    value = state.subSeed,
                    color = colorOddText,
                    onCopyTextClick = onCopyTextClick,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_sub_seed_strength.asUiText(),
                    value = state.subSeedStrength,
                    color = colorEvenText,
                    onCopyTextClick = onCopyTextClick,
                )
                if (state.generationType == AiGenerationResult.Type.IMAGE_TO_IMAGE) GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_denoising_strength.asUiText(),
                    value = state.denoisingStrength,
                    color = colorOddText,
                    onCopyTextClick = onCopyTextClick,
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 2.dp),
            ) {
                Row {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onSendToTxt2Img,
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_send_to_txt2img),
                            textAlign = TextAlign.Center,
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onSendToImg2Img,
                    ) {
                        Text(
                            text = stringResource(id = R.string.action_send_to_img2img),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Row {
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
            }

        },
    )
}

@Composable
private fun GalleryDetailRow(
    modifier: Modifier = Modifier,
    column1Weight: Float = 0.4f,
    column2Weight: Float = 0.6f,
    name: UiText,
    value: UiText,
    color: Color,
    onCopyTextClick: (CharSequence) -> Unit = {},
) {
    val rawValue = value.asString()
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
        if (rawValue.isNotBlank()) {
            IconButton(
                onClick = { onCopyTextClick(rawValue) },
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
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

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewGalleryScreenTxt2ImgContentTabImage() {
    ScreenContent(state = mockGalleryDetailTxt2Img)
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewGalleryScreenTxt2ImgContentTabInfo() {
    ScreenContent(state = mockGalleryDetailTxt2Img.copy(selectedTab = GalleryDetailState.Tab.INFO))
}
