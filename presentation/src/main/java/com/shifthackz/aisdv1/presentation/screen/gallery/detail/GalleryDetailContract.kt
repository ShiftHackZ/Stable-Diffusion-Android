package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

sealed interface GalleryDetailEffect : MviEffect {}

sealed interface GalleryDetailState : MviState {

    object Loading : GalleryDetailState

    data class Content(
        val id: Long,
        val bitmap: Bitmap,
    ) : GalleryDetailState
}
