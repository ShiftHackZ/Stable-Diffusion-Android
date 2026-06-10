package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

/**
 * Carries `GalleryState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class GalleryState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `loadingNextPage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loadingNextPage: Boolean = false,
    /**
     * Exposes the `items` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val items: List<GalleryGridItemUi> = emptyList(),
    /**
     * Exposes the `nextPage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val nextPage: Int = 0,
    /**
     * Exposes the `canLoadMore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val canLoadMore: Boolean = true,
    /**
     * Exposes the `dialog` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val dialog: GalleryDialog = GalleryDialog.None,
    /**
     * Exposes the `mediaStoreInfo` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mediaStoreInfo: MediaStoreInfo = MediaStoreInfo(),
    /**
     * Exposes the `dropdownMenuShow` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val dropdownMenuShow: Boolean = false,
    /**
     * Exposes the `selectionMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selectionMode: Boolean = false,
    /**
     * Exposes the `selection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val selection: List<Long> = emptyList(),
    /**
     * Exposes the `grid` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val grid: Grid = Grid.Fixed2,
) : MviState

/**
 * Defines the `GalleryDialog` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
sealed interface GalleryDialog {
    /**
     * Provides the `None` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : GalleryDialog
    /**
     * Provides the `DeleteAllConfirm` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DeleteAllConfirm : GalleryDialog
    /**
     * Provides the `DeleteSelectionConfirm` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DeleteSelectionConfirm : GalleryDialog
    /**
     * Carries `ConfirmExport` data through the SDAI presentation layer.
     *
     * @param exportAll export all value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ConfirmExport(val exportAll: Boolean) : GalleryDialog
    /**
     * Provides the `ExportInProgress` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ExportInProgress : GalleryDialog
    /**
     * Carries `Error` data through the SDAI presentation layer.
     *
     * @param message message value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Error(val message: String) : GalleryDialog
}

/**
 * Carries `GalleryGridItemUi` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class GalleryGridItemUi(
    /**
     * Exposes the `id` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val id: Long,
    /**
     * Exposes the `image` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val image: ImageBitmap?,
    /**
     * Exposes the `hidden` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hidden: Boolean,
)
