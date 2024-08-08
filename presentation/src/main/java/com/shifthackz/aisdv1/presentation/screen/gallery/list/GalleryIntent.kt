package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.net.Uri
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.android.core.mvi.MviIntent

sealed interface GalleryIntent : MviIntent {

    enum class Export : GalleryIntent {
        Request, Confirm;
    }

    enum class DeleteSelection : GalleryIntent {
        Request, Confirm;
    }

    data object DismissDialog : GalleryIntent

    data class OpenItem(val item: GalleryGridItemUi) : GalleryIntent

    data class OpenMediaStoreFolder(val uri: Uri) : GalleryIntent

    data class Drawer(val intent: DrawerIntent) : GalleryIntent

    data class ChangeSelectionMode(val flag: Boolean) : GalleryIntent

    data object UnselectAll : GalleryIntent

    data class ToggleItemSelection(val id: Long) : GalleryIntent
}
