package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import com.shifthackz.aisdv1.core.ui.MviEffect
import java.io.File

sealed interface GalleryDetailEffect : MviEffect {

    data class ShareImageFile(val file: File) : GalleryDetailEffect

    data class ShareGenerationParams(val state: GalleryDetailState) : GalleryDetailEffect

    data class ShareClipBoard(val text: String) : GalleryDetailEffect
}
