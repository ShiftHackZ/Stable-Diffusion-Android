@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.v2

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryItemUi
import com.shifthackz.aisdv1.presentation.widget.ad.AdMobBanner
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GallerySlideScreen(
    private val viewModel: GallerySlideViewModel,
    private val onNavigateBack: () -> Unit = {},
) : MviScreen<GalleryStateV2, GalleryDetailEffect>(viewModel), KoinComponent {

    private val adFeature: AdFeature by inject()

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            pagingFlow = viewModel.pagingFlow,
            adFeature = adFeature,
            onNavigateBack = onNavigateBack,
            onTabSelected = viewModel::selectTab,
//            onPageSelected = viewModel::onPageSelected,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryStateV2,
    pagingFlow: Flow<PagingData<GalleryItemUi>>,
    adFeature: AdFeature,
    onNavigateBack: () -> Unit = {},
    onTabSelected: (GalleryTab) -> Unit = {},
//    onPageSelected: (Int) -> Unit = {},
) {
    if (state !is GalleryStateV2.Initialized) return
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
//                        IconButton(
//                            onClick = onExportImageToolbarClick,
//                            content = {
//                                Image(
//                                    modifier = Modifier.size(24.dp),
//                                    painter = painterResource(id = R.drawable.ic_share),
//                                    contentDescription = "Export",
//                                    colorFilter = ColorFilter.tint(LocalContentColor.current),
//                                )
//                            },
//                        )
                    }
                )
            },
            content = { paddingValues ->
                val contentModifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)

                val images = pagingFlow.collectAsLazyPagingItems()

                val pagerState = rememberPagerState(initialPage = state.initialIndex)
//                LaunchedEffect(pagerState) {
//                    snapshotFlow {
//                        pagerState.currentPage
//                    }.collect {
//                        onPageSelected(it)
//                    }
//                }

                when {
                    images.itemCount > 0 -> when (state.selectedTab) {
                        GalleryTab.IMAGE -> HorizontalPager(
                            modifier = contentModifier,
                            pageCount = state.totalPageCount,
                            state = pagerState,
                            key = { state.keys[it] }
                        ) { index ->
                            images[index]?.let { itemUi ->
                                Image(
                                    modifier = Modifier.fillMaxSize(),
                                    contentDescription = null,
                                    bitmap = itemUi.bitmap.asImageBitmap(),
                                )
                            }
                        }
                        GalleryTab.INFO -> images[pagerState.currentPage]?.let { itemUi ->
                            GalleryDetailsTable(
                                modifier = contentModifier,
                                result = itemUi.ai,
                            )
                        }
                    }
                }
            },
            bottomBar = {
                GalleryDetailNavigationBar(adFeature, state.selectedTab, onTabSelected)
            },
        )
//        when (state.screenDialog) {
//            GalleryDetailState.Dialog.DeleteConfirm -> DecisionInteractiveDialog(
//                title = R.string.interaction_delete_generation_title.asUiText(),
//                text = R.string.interaction_delete_generation_sub_title.asUiText(),
//                confirmActionResId = R.string.yes,
//                dismissActionResId = R.string.no,
//                onConfirmAction = onDeleteConfirmClick,
//                onDismissRequest = onDismissScreenDialog,
//            )
//            GalleryDetailState.Dialog.None -> Unit
//        }
    }
}

@Composable
private fun GalleryDetailNavigationBar(
    adFeature: AdFeature,
    selectedTab: GalleryTab,
    onTabSelected: (GalleryTab) -> Unit,
) {
    Column {
        AdMobBanner(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            adFeature = adFeature,
            adFactory = adFeature::getGalleryDetailBannerAd,
        )
        NavigationBar {
            GalleryTab.values().forEach { tab ->
                NavigationBarItem(
                    selected = selectedTab == tab,
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
private fun GalleryDetailsTable(
    modifier: Modifier = Modifier,
    result: AiGenerationResult,
    onSendToTxt2Img: () -> Unit = {},
    onSendToImg2Img: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    onExportParamsClick: (AiGenerationResult) -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        content = { paddingValues ->
            Column(
                modifier = Modifier
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
                    value = "${result.createdAt}".asUiText(),
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_type.asUiText(),
                    value = result.type.key.asUiText(),
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_prompt.asUiText(),
                    value = result.prompt.asUiText(),
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_negative_prompt.asUiText(),
                    value = result.negativePrompt.asUiText(),
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_size.asUiText(),
                    value = "${result.width} X ${result.height}".asUiText(),
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_sampling_steps.asUiText(),
                    value = "${result.samplingSteps}".asUiText(),
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_cfg.asUiText(),
                    value = "${result.cfgScale}".asUiText(),
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_restore_faces.asUiText(),
                    value = "${result.restoreFaces}".asUiText(),
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_sampler.asUiText(),
                    value = result.sampler.asUiText(),
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_seed.asUiText(),
                    value = result.seed.asUiText(),
                    color = colorEvenText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_sub_seed.asUiText(),
                    value = result.subSeed.asUiText(),
                    color = colorOddText,
                )
                GalleryDetailRow(
                    modifier = Modifier.background(color = colorEvenBg),
                    name = R.string.gallery_info_field_sub_seed_strength.asUiText(),
                    value = "${result.subSeedStrength}".asUiText(),
                    color = colorEvenText,
                )
                if (result.type == AiGenerationResult.Type.IMAGE_TO_IMAGE) GalleryDetailRow(
                    modifier = Modifier.background(color = colorOddBg),
                    name = R.string.gallery_info_field_denoising_strength.asUiText(),
                    value = "${result.denoisingStrength}".asUiText(),
                    color = colorOddText,
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
                        onClick = { onExportParamsClick(result) },
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
