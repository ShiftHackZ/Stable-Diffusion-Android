@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.shifthackz.aisdv1.core.extensions.items
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R

class GalleryScreen(
    private val viewModel: GalleryViewModel,
) : MviScreen<GalleryState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            lazyGalleryItems = viewModel.pagingFlow.collectAsLazyPagingItems(),
            onExportDataClick = viewModel::exportData,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryState,
    lazyGalleryItems: LazyPagingItems<GalleryGridItemUi>,
    onExportDataClick: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(stringResource(id = R.string.title_gallery))
                    },
                    actions = {
                        IconButton(
                            onClick = onExportDataClick,
                            content = {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_share),
                                    contentDescription = "Export"
                                )
                            },
                        )
                    },
                )
            },
            content = { paddingValues ->
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(lazyGalleryItems) { galleryItemUi ->
                        galleryItemUi?.run {
                            GalleryUiItem(
                                item = this,
                            )
                        }
                    }
                }
            }
        )
        when (state.screenDialog) {
            GalleryState.ScreenDialog.None -> Unit
        }
    }
}

@Composable
private fun GalleryUiItem(
    item: GalleryGridItemUi,

    ) {
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp)),
        bitmap = item.bitmap.asImageBitmap(),
        contentScale = ContentScale.Crop,
        contentDescription = "gallery_item",
    )
}
