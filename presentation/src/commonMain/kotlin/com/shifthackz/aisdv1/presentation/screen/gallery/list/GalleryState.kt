package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

@Immutable
data class GalleryState(
    val loading: Boolean = true,
    val loadingNextPage: Boolean = false,
    val items: List<GalleryGridItemUi> = emptyList(),
    val nextPage: Int = 0,
    val canLoadMore: Boolean = true,
    val dialog: GalleryDialog = GalleryDialog.None,
    val mediaStoreInfo: MediaStoreInfo = MediaStoreInfo(),
    val dropdownMenuShow: Boolean = false,
    val selectionMode: Boolean = false,
    val selection: List<Long> = emptyList(),
    val grid: Grid = Grid.Fixed2,
) : MviState

@Immutable
sealed interface GalleryDialog {
    data object None : GalleryDialog
    data object DeleteAllConfirm : GalleryDialog
    data object DeleteSelectionConfirm : GalleryDialog
    data class ConfirmExport(val exportAll: Boolean) : GalleryDialog
    data object ExportInProgress : GalleryDialog
    data class Error(val message: String) : GalleryDialog
}

@Immutable
data class GalleryGridItemUi(
    val id: Long,
    val image: ImageBitmap?,
    val hidden: Boolean,
)
