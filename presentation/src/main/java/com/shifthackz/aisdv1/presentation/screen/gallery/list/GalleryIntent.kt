package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.net.Uri
import com.shifthackz.aisdv1.core.ui.MviIntent

sealed interface GalleryIntent : MviIntent {
    /*
     onExportToolbarClick: () -> Unit = {},
    onExportConfirmClick: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
    onGalleryItemClick: (GalleryGridItemUi) -> Unit = {},
    onOpenMediaStoreFolder: (Uri) -> Unit = {},
     */
    enum class Export : GalleryIntent {
        Request, Confirm;
    }

    data object DismissDialog : GalleryIntent

    data class OpenItem(val item: GalleryGridItemUi) : GalleryIntent

    data class OpenMediaStoreFolder(val uri: Uri): GalleryIntent
}
