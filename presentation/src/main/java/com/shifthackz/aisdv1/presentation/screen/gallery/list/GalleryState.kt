package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState

@Immutable
data class GalleryState(
    val screenModal: Modal = Modal.None,
    val mediaStoreInfo: MediaStoreInfo = MediaStoreInfo(),
) : MviState

data class GalleryGridItemUi(
    val id: Long,
    val bitmap: Bitmap,
)
