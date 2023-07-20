@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.shifthackz.aisdv1.core.extensions.items
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import kotlinx.coroutines.flow.Flow
import java.io.File

class GalleryScreen(
    private val viewModel: GalleryViewModel,
    private val shareGalleryFile: (File) -> Unit = {},
    private val openGalleryItemDetails: (Long) -> Unit = {},
    private val launchIntent: (Intent) -> Unit = {},
) : MviScreen<GalleryState, GalleryEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsStateWithLifecycle().value,
            pagingFlow = viewModel.pagingFlow,
            onExportToolbarClick = viewModel::launchGalleryExportConfirmation,
            onExportConfirmClick = viewModel::launchGalleryExport,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
            onGalleryItemClick = { item -> openGalleryItemDetails(item.id) },
            onOpenMediaStoreFolder = ::onOpenMediaStoreFolder,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit

    override fun processEffect(effect: GalleryEffect) = when (effect) {
        is GalleryEffect.Share -> shareGalleryFile(effect.zipFile)
    }

    private fun onOpenMediaStoreFolder(uri: Uri) = with(Intent(Intent.ACTION_VIEW)) {
        setDataAndType(uri, DocumentsContract.Document.MIME_TYPE_DIR)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        launchIntent(this)
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryState,
    pagingFlow: Flow<PagingData<GalleryGridItemUi>>,
    onExportToolbarClick: () -> Unit = {},
    onExportConfirmClick: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
    onGalleryItemClick: (GalleryGridItemUi) -> Unit = {},
    onOpenMediaStoreFolder: (Uri) -> Unit = {},
) {
    val listState = rememberLazyGridState()
    val lazyGalleryItems = pagingFlow.collectAsLazyPagingItems()

    val emptyStatePredicate: () -> Boolean = {
        lazyGalleryItems.loadState.refresh is LoadState.NotLoading
                && lazyGalleryItems.itemCount == 0
                && lazyGalleryItems.loadState.append.endOfPaginationReached
    }

    Box(modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_gallery),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    actions = {
                        if (lazyGalleryItems.itemCount > 0) IconButton(
                            onClick = onExportToolbarClick,
                            content = {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_share),
                                    contentDescription = "Export",
                                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                                )
                            },
                        )
                    },
                )
            },
            bottomBar = {
                state.mediaStoreInfo.takeIf(MediaStoreInfo::isNotEmpty)?.let { info ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp, start = 16.dp)
                                .fillMaxWidth(0.65f),
                            text = stringResource(
                                id = R.string.gallery_media_store_banner,
                                "${info.count}",
                            ),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            modifier = Modifier.padding(bottom = 4.dp, end = 16.dp),
                            onClick = {
                                state.mediaStoreInfo.folderUri?.let(onOpenMediaStoreFolder::invoke)
                            },
                        ) {
                            Text(text = stringResource(id = R.string.browse).toUpperCase(Locale.current))
                        }
                    }
                }
            },
            content = { paddingValues ->
                when {
                    emptyStatePredicate() -> GalleryEmptyState(Modifier.fillMaxSize())
                    lazyGalleryItems.itemCount == 0 -> LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        (1..6).map {
                            item(it) {
                                GalleryUiItemShimmer()
                            }
                        }
                    }
                    else -> LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        state = listState,
                    ) {
                        items(lazyGalleryItems) { galleryItemUi ->
                            if (galleryItemUi != null) {
                                GalleryUiItem(
                                    item = galleryItemUi,
                                    onClick = onGalleryItemClick,
                                )
                            } else {
                                GalleryUiItemShimmer()
                            }
                        }
                    }
                }
            }
        )
        when (state.screenDialog) {
            GalleryState.Dialog.None -> Unit
            GalleryState.Dialog.ConfirmExport -> DecisionInteractiveDialog(
                title = R.string.interaction_export_title.asUiText(),
                text = R.string.interaction_export_sub_title.asUiText(),
                confirmActionResId = R.string.action_export,
                onConfirmAction = onExportConfirmClick,
                onDismissRequest = onDismissScreenDialog,
            )
            GalleryState.Dialog.ExportInProgress -> ProgressDialog(
                titleResId = R.string.exporting_progress_title,
                subTitleResId = R.string.exporting_progress_sub_title,
                canDismiss = false,
            )
            is GalleryState.Dialog.Error -> ErrorDialog(
                text = state.screenDialog.error,
                onDismissScreenDialog,
            )
        }
    }
}

@Composable
private fun GalleryUiItem(
    item: GalleryGridItemUi,
    onClick: (GalleryGridItemUi) -> Unit = {},
) {
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(item) },
        bitmap = item.bitmap.asImageBitmap(),
        contentScale = ContentScale.Crop,
        contentDescription = "gallery_item",
    )
}

@Composable
private fun GalleryUiItemShimmer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .shimmer()
    )
}

@Composable
private fun GalleryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.gallery_empty_title),
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.gallery_empty_sub_title),
            textAlign = TextAlign.Center,
        )
    }
}
