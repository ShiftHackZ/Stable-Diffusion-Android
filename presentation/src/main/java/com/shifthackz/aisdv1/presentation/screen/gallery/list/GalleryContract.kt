package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import java.io.File

sealed interface GalleryEffect : MviEffect {
    data class Share(val zipFile: File) : GalleryEffect
}

data class GalleryState(
    val screenDialog: Dialog = Dialog.None,
    val mediaStoreInfo: MediaStoreInfo = MediaStoreInfo(),
) : MviState {

    sealed interface Dialog {
        data object None : Dialog

        data object ConfirmExport : Dialog

        data object ExportInProgress : Dialog

        data class Error(val error: UiText) : Dialog
    }
}

data class GalleryGridItemUi(
    val id: Long,
    val bitmap: Bitmap,
)
