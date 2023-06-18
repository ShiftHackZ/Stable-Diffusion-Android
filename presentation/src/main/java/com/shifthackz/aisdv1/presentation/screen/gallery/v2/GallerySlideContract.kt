package com.shifthackz.aisdv1.presentation.screen.gallery.v2

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.presentation.R
import java.io.File

sealed interface GalleryDetailEffect : MviEffect {

    object NavigateBack : GalleryDetailEffect

    data class ShareImageFile(val file: File) : GalleryDetailEffect
}

interface GalleryStateV2 : MviState {
    val modal: GalleryStateV2.Modal

    object Uninitialized : GalleryStateV2 {
        override val modal = Modal.None
    }

    data class Initialized(
        override val modal: Modal = Modal.None,
        val selectedTab: GalleryTab = GalleryTab.IMAGE,
        val keys: List<Long>,
        val initialIndex: Int = 0,
        val totalPageCount: Int = 0,
    ) : GalleryStateV2

    sealed interface Modal {
        object None : Modal
        object DeleteConfirm : Modal
    }
}

sealed interface GalleryTabState<out T : Any> {
    object Loading : GalleryTabState<Nothing>
    data class Content<out T : Any>(val data: T) : GalleryTabState<T>
}

data class GalleryImageUi(
    val bmpOutput: Bitmap,
    val bmpInput: Bitmap?,
)

enum class GalleryTab(
    @StringRes val label: Int,
    @DrawableRes val iconRes: Int,
) {
    IMAGE(R.string.gallery_tab_image, R.drawable.ic_image),
    INFO(R.string.gallery_tab_info, R.drawable.ic_text);
}
