package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.ui.MviIntent

sealed interface GalleryDetailIntent : MviIntent {
    /*
    onNavigateBack: () -> Unit = {},
    onTabSelected: (GalleryDetailState.Tab) -> Unit = {},
    onCopyTextClick: (CharSequence) -> Unit = {},
    onSendToTxt2Img: () -> Unit = {},
    onSendToImg2Img: () -> Unit = {},
    onExportImageToolbarClick: () -> Unit = {},
    onExportParamsClick: (GalleryDetailState.Content) -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    onDeleteConfirmClick: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
     */
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

    data object DismissDialog : GalleryDetailIntent
}
