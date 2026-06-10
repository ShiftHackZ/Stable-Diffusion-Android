package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.mvi.MviIntent

sealed interface GalleryDetailIntent : MviIntent {

    data object NavigateBack : GalleryDetailIntent

    data class SelectTab(val tab: GalleryDetailTab) : GalleryDetailIntent

    data class CopyToClipboard(val content: String) : GalleryDetailIntent

    enum class SendTo : GalleryDetailIntent {
        Img2Img,
        Txt2Img,
    }

    enum class Export : GalleryDetailIntent {
        Image,
        Params,
    }

    enum class Share : GalleryDetailIntent {
        Image,
        Params,
    }

    enum class Delete : GalleryDetailIntent {
        Request,
        Confirm,
    }

    data object ToggleVisibility : GalleryDetailIntent

    data object Report : GalleryDetailIntent

    data object DismissDialog : GalleryDetailIntent
}
