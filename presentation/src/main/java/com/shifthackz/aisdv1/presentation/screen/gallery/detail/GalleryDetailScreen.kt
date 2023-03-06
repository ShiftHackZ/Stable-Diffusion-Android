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
import com.shifthackz.aisdv1.presentation.widget.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.ZoomableImageSource
import java.io.File

class GalleryDetailScreen(
    private val viewModel: GalleryDetailViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val shareGalleryFile: (File) -> Unit = {},
) : MviScreen<GalleryDetailState, GalleryDetailEffect>(viewModel) {

    override val statusBarColor: Color = Color.White
    override val navigationBarColor: Color = Color.Black

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onNavigateBack = onNavigateBack,
            onTabSelected = viewModel::selectTab,
            onExportToolbarClick = viewModel::share,
        )
    }

    override fun processEffect(effect: GalleryDetailEffect) = when (effect) {
        is GalleryDetailEffect.ShareImageFile -> shareGalleryFile(effect.file)
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryDetailState,
    onNavigateBack: () -> Unit = {},
    onTabSelected: (GalleryDetailState.Tab) -> Unit = {},
    onExportToolbarClick: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
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
                )
                is GalleryDetailState.Loading -> Text("Load")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = state.tab == GalleryDetailState.Tab.IMAGE,
                    label = {
                        Text(stringResource(id = R.string.gallery_tab_image))
                    },
                    icon = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_image),
                            contentDescription = stringResource(id = R.string.gallery_tab_image),
                        )
                    },
                    onClick = {
                        onTabSelected(GalleryDetailState.Tab.IMAGE)
                    }
                )
                NavigationBarItem(
                    selected = state.tab == GalleryDetailState.Tab.INFO,
                    label = {
                        Text(stringResource(id = R.string.gallery_tab_info))
                    },
                    icon = {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(R.drawable.ic_text),
                            contentDescription = stringResource(id = R.string.gallery_tab_info),
                        )
                    },
                    onClick = {
                        onTabSelected(GalleryDetailState.Tab.INFO)
                    }
                )
            }
        }
    )
}

@Composable
private fun GalleryDetailContentState(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
) {
    Column(
        modifier = modifier,
    ) {
        when (state.tab) {
            GalleryDetailState.Tab.IMAGE -> ZoomableImage(
                modifier = Modifier.fillMaxSize(),
                source = ZoomableImageSource.Bmp(state.bitmap),
            )
            GalleryDetailState.Tab.INFO -> GalleryDetailsTable(
                modifier = Modifier.fillMaxSize(),
                state = state,
            )
        }
    }
}

@Composable
private fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .padding(16.dp),
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
