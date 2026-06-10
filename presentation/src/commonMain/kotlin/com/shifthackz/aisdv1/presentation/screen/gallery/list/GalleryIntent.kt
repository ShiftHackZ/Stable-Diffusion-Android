package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.mvi.MviIntent

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
        enum class All : Delete {
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

    data object LoadNextPage : GalleryIntent

    data object OpenDrawer : GalleryIntent

    data object OpenMediaStoreFolder : GalleryIntent

    data class OpenItem(val id: Long) : GalleryIntent

    data class ChangeSelectionMode(val flag: Boolean) : GalleryIntent

    data object UnselectAll : GalleryIntent

    data class ToggleItemSelection(val id: Long) : GalleryIntent
}
