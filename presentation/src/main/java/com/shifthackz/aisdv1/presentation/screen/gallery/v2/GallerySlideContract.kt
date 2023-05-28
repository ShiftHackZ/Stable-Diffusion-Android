package com.shifthackz.aisdv1.presentation.screen.gallery.v2

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import java.io.File

typealias TabImageState = GalleryTabState<GalleryImageUi>
typealias TabInfoState = GalleryTabState<AiGenerationResult>

sealed interface GalleryDetailEffect : MviEffect {

    object NavigateBack : GalleryDetailEffect

    data class ShareImageFile(val file: File) : GalleryDetailEffect
}

interface GalleryStateV2 : MviState {

    object Uninitialized : GalleryStateV2

    data class Initialized(
        val selectedTab: GalleryTab = GalleryTab.IMAGE,
//        val tabImageState: TabImageState = GalleryTabState.Loading,
//        val tabContentState: TabInfoState = GalleryTabState.Loading,
//        val images: List<TabImageState>,
        val keys: List<Long>,
        val initialIndex: Int = 0,
        val totalPageCount: Int = 0,
    ) : GalleryStateV2
}

sealed interface GalleryTabState<out T : Any> {
    object Loading : GalleryTabState<Nothing>
    data class Content<out T : Any>(val data: T) : GalleryTabState<T>
}

data class GalleryImageUi(
    val bmpOutput: Bitmap,
    val bmpInput: Bitmap?,
)

enum class GalleryTab(
    @StringRes val label: Int,
    @DrawableRes val iconRes: Int,
) {
    IMAGE(R.string.gallery_tab_image, R.drawable.ic_image),
    INFO(R.string.gallery_tab_info, R.drawable.ic_text);
}
