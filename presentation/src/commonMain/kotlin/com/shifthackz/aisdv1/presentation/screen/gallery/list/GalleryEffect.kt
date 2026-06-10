package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.mvi.MviEffect

/**
 * Defines the `GalleryEffect` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface GalleryEffect : MviEffect {
    /**
     * Carries `OpenMediaStoreFolder` data through the SDAI presentation layer.
     *
     * @param folderUri folder uri value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class OpenMediaStoreFolder(val folderUri: String) : GalleryEffect
    /**
     * Carries `ShareExport` data through the SDAI presentation layer.
     *
     * @param filePath file path value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class ShareExport(val filePath: String) : GalleryEffect
}
