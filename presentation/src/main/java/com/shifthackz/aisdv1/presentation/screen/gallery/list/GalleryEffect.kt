package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.net.Uri
import com.shifthackz.android.core.mvi.MviEffect
import java.io.File

sealed interface GalleryEffect : MviEffect {

    data object Refresh : GalleryEffect

    data class Share(val zipFile: File) : GalleryEffect

    data class OpenUri(val uri: Uri) : GalleryEffect
}
