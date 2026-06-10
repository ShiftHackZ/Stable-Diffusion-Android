package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.mvi.MviIntent

/**
 * Defines the `GalleryIntent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GalleryIntent : MviIntent {

    /**
     * Defines the `Export` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Export : GalleryIntent {
        /**
         * Coordinates `All` behavior in the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        enum class All : Export {
            Request, Confirm;
        }

        /**
         * Coordinates `Selection` behavior in the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        enum class Selection : Export {
            Request, Confirm;
        }
    }

    /**
     * Defines the `Delete` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    sealed interface Delete : GalleryIntent {
        /**
         * Coordinates `All` behavior in the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        enum class All : Delete {
            Request, Confirm;
        }

        /**
         * Coordinates `Selection` behavior in the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        enum class Selection : Delete {
            Request, Confirm;
        }
    }

    /**
     * Coordinates `Dropdown` behavior in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    enum class Dropdown : GalleryIntent {
        Toggle, Show, Close;
    }

    /**
     * Provides the `DismissDialog` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object DismissDialog : GalleryIntent

    /**
     * Provides the `LoadNextPage` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object LoadNextPage : GalleryIntent

    /**
     * Provides the `OpenDrawer` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenDrawer : GalleryIntent

    /**
     * Provides the `OpenMediaStoreFolder` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object OpenMediaStoreFolder : GalleryIntent

    /**
     * Carries `OpenItem` data through the SDAI presentation layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    data class OpenItem(val id: Long) : GalleryIntent

    /**
     * Carries `ChangeSelectionMode` data through the SDAI presentation layer.
     *
     * @param flag flag value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ChangeSelectionMode(val flag: Boolean) : GalleryIntent

    /**
     * Provides the `UnselectAll` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object UnselectAll : GalleryIntent

    /**
     * Carries `ToggleItemSelection` data through the SDAI presentation layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    data class ToggleItemSelection(val id: Long) : GalleryIntent
}
