package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.net.Uri
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.android.core.mvi.MviIntent

sealed interface GalleryIntent : MviIntent {

    sealed interface Export : GalleryIntent {

        enum class All : Export {
            Request, Confirm;
        }

        enum class Selection : Export {
            Request, Confirm;
        }
    }

    sealed interface Delete : GalleryIntent {

        enum class All : Export {
            Request, Confirm;
        }

        enum class Selection : Delete {
            Request, Confirm;
        }
    }

    enum class Dropdown : GalleryIntent {
        Toggle, Show, Close;
    }

    data object DismissDialog : GalleryIntent

    data class OpenItem(val item: GalleryGridItemUi) : GalleryIntent

    data class OpenMediaStoreFolder(val uri: Uri) : GalleryIntent

    data class Drawer(val intent: DrawerIntent) : GalleryIntent

    data class ChangeSelectionMode(val flag: Boolean) : GalleryIntent

    data object UnselectAll : GalleryIntent

    data class ToggleItemSelection(val id: Long) : GalleryIntent
}
