package com.shifthackz.aisdv1.presentation.screen.gallery

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.ui.MviState

sealed interface GalleryState : MviState {

    val screenDialog: ScreenDialog

    data class Paginated(
        override val screenDialog: ScreenDialog = ScreenDialog.None,
    ) : GalleryState

    sealed interface ScreenDialog {
        object None : ScreenDialog
    }
}

data class GalleryGridItemUi(val bitmap: Bitmap)
