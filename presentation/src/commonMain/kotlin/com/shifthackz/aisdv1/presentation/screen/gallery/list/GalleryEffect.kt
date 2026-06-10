package com.shifthackz.aisdv1.presentation.screen.gallery.list

import com.shifthackz.aisdv1.core.mvi.MviEffect

sealed interface GalleryEffect : MviEffect {
    data class OpenMediaStoreFolder(val folderUri: String) : GalleryEffect
    data class ShareExport(val filePath: String) : GalleryEffect
}
