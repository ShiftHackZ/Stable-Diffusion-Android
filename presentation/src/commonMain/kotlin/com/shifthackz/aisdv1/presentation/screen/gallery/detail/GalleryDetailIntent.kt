package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `GalleryDetailIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GalleryDetailIntent : MviIntent {

    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : GalleryDetailIntent

    /**
     * Carries `SelectTab` data through the SDAI presentation layer.
     *
     * @param tab tab value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class SelectTab(val tab: GalleryDetailTab) : GalleryDetailIntent

    /**
     * Carries `CopyToClipboard` data through the SDAI presentation layer.
     *
     * @param content content value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class CopyToClipboard(val content: String) : GalleryDetailIntent

    /**
     * Coordinates `SendTo` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class SendTo : GalleryDetailIntent {
        Img2Img,
        Txt2Img,
    }

    /**
     * Coordinates `Export` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Export : GalleryDetailIntent {
        Image,
        Params,
    }

    /**
     * Coordinates `Share` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Share : GalleryDetailIntent {
        Image,
        Params,
    }

    /**
     * Coordinates `Delete` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Delete : GalleryDetailIntent {
        Request,
        Confirm,
    }

    /**
     * Provides the `ToggleVisibility` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ToggleVisibility : GalleryDetailIntent

    /**
     * Provides the `Report` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object Report : GalleryDetailIntent

    /**
     * Provides the `DismissDialog` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissDialog : GalleryDetailIntent
}
