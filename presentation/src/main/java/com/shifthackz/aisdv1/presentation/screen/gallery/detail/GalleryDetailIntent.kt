package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.android.core.mvi.MviIntent

sealed interface GalleryDetailIntent : MviIntent {

    data object NavigateBack : GalleryDetailIntent

    data class SelectTab(val tab: GalleryDetailState.Tab) : GalleryDetailIntent

    data class CopyToClipboard(val content: CharSequence) : GalleryDetailIntent

    enum class SendTo : GalleryDetailIntent {
        Img2Img, Txt2Img;
    }

    enum class Export : GalleryDetailIntent {
        Image, Params;
    }

    enum class Delete : GalleryDetailIntent {
        Request, Confirm
    }

    data object ToggleVisibility : GalleryDetailIntent

    data object Report : GalleryDetailIntent

    data object DismissDialog : GalleryDetailIntent
}
