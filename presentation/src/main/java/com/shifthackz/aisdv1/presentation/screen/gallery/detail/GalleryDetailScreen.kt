@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.sharing.shareFile
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.theme.colors
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.image.ZoomableImageSource
import com.shifthackz.catppuccin.palette.Catppuccin
import org.koin.androidx.compose.getViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.R as PresentationR

@Composable
fun GalleryDetailScreen(itemId: Long) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val fileProviderDescriptor: FileProviderDescriptor = koinInject()
    val galleryDetailSharing: GalleryDetailSharing = koinInject()
    MviComponent(
        viewModel = getViewModel<GalleryDetailViewModel>(
            parameters = { parametersOf(itemId) },
        ),
        processEffect = { effect ->
            when (effect) {
                is GalleryDetailEffect.ShareImageFile -> context.shareFile(
                    file = effect.file,
                    fileProviderPath = fileProviderDescriptor.providerPath,
                    fileMimeType = Constants.MIME_TYPE_JPG,
                )

                is GalleryDetailEffect.ShareGenerationParams -> galleryDetailSharing(
                    context = context,
                    state = effect.state,
                )

                is GalleryDetailEffect.ShareClipBoard -> {
                    clipboardManager.setText(AnnotatedString(effect.text))
                }
            }
        },
        applySystemUiColors = true,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    ) { state, intentHandler ->
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            processIntent = intentHandler,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryDetailState,
    processIntent: (GalleryDetailIntent) -> Unit = {},
) {
    Box(modifier = modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(id = LocalizationR.string.title_gallery_details))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                processIntent(GalleryDetailIntent.NavigateBack)
                            },
                            content = {
                                Icon(
                                    Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Back button",
                                )
                            },
                        )
                    },
                    actions = {
                        AnimatedVisibility(
                            visible = state.selectedTab != GalleryDetailState.Tab.INFO,
                            enter = fadeIn(),
                            exit = fadeOut(),
                        ) {
                            IconButton(
                                onClick = { processIntent(GalleryDetailIntent.Export.Image) },
                                content = {
                                    Image(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(id = PresentationR.drawable.ic_share),
                                        contentDescription = "Export",
                                        colorFilter = ColorFilter.tint(LocalContentColor.current),
                                    )
                                },
                            )
                        }
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
                        onCopyTextClick = {
                            processIntent(GalleryDetailIntent.CopyToClipboard(it))
                        },
                    )

                    is GalleryDetailState.Loading -> Unit
                }
            },
            bottomBar = {
                GalleryDetailNavigationBar(
                    state = state,
                    processIntent = processIntent,
                )
            },
        )
        ModalRenderer(screenModal = state.screenModal) {
            (it as? GalleryDetailIntent)?.let(processIntent::invoke)
        }
    }
}

@Composable
private fun GalleryDetailNavigationBar(
    state: GalleryDetailState,
    processIntent: (GalleryDetailIntent) -> Unit = {},
) {
    Column {
        if (state is GalleryDetailState.Content) {
            Row(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    onClick = { processIntent(GalleryDetailIntent.SendTo.Txt2Img) },
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = PresentationR.drawable.ic_text),
                        contentDescription = "txt2img",
                        tint = LocalContentColor.current,
                    )
                }
                IconButton(
                    onClick = { processIntent(GalleryDetailIntent.SendTo.Img2Img) },
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = PresentationR.drawable.ic_image),
                        contentDescription = "img2img",
                        tint = LocalContentColor.current,
                    )
                }
                IconButton(
                    onClick = { processIntent(GalleryDetailIntent.Export.Params) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share prompt",
                    )
                }
                IconButton(
                    onClick = { processIntent(GalleryDetailIntent.Delete.Request) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                    )
                }
            }
        }
        NavigationBar {
            state.tabs.forEach { tab ->
                NavigationBarItem(
                    selected = state.selectedTab == tab,
                    label = {
                        Text(
                            text = stringResource(id = tab.label),
                            color = LocalContentColor.current,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors().copy(
                        selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    icon = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(tab.iconRes),
                            contentDescription = stringResource(id = LocalizationR.string.gallery_tab_image),
                            colorFilter = ColorFilter.tint(LocalContentColor.current),
                        )
                    },
                    onClick = { processIntent(GalleryDetailIntent.SelectTab(tab)) },
                )
            }
        }
    }

}

@Composable
private fun GalleryDetailContentState(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
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
                onCopyTextClick = onCopyTextClick,
            )
        }
    }
}

@Composable
private fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
    onCopyTextClick: (CharSequence) -> Unit = {},
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
        val colorOddBg = MaterialTheme.colorScheme.surface
        val colorOddText = colors(
            light = Catppuccin.Latte.Text,
            dark = Catppuccin.Frappe.Text
        )
        val colorEvenBg = MaterialTheme.colorScheme.surfaceTint
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_date.asUiText(),
            value = state.createdAt,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = LocalizationR.string.gallery_info_field_type.asUiText(),
            value = state.type,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_prompt.asUiText(),
            value = state.prompt,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = LocalizationR.string.gallery_info_field_negative_prompt.asUiText(),
            value = state.negativePrompt,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_size.asUiText(),
            value = state.size,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = LocalizationR.string.gallery_info_field_sampling_steps.asUiText(),
            value = state.samplingSteps,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_cfg.asUiText(),
            value = state.cfgScale,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = LocalizationR.string.gallery_info_field_restore_faces.asUiText(),
            value = state.restoreFaces,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_sampler.asUiText(),
            value = state.sampler,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = LocalizationR.string.gallery_info_field_seed.asUiText(),
            value = state.seed,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_sub_seed.asUiText(),
            value = state.subSeed,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        GalleryDetailRow(
            modifier = Modifier.background(color = colorEvenBg),
            name = LocalizationR.string.gallery_info_field_sub_seed_strength.asUiText(),
            value = state.subSeedStrength,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
        if (state.generationType == AiGenerationResult.Type.IMAGE_TO_IMAGE) GalleryDetailRow(
            modifier = Modifier.background(color = colorOddBg),
            name = LocalizationR.string.gallery_info_field_denoising_strength.asUiText(),
            value = state.denoisingStrength,
            color = colorOddText,
            onCopyTextClick = onCopyTextClick,
        )
    }
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
        modifier = modifier
            .padding(start = 12.dp)
            .padding(vertical = 8.dp),
        text = text.asString(),
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
