package com.shifthackz.aisdv1.domain.entity

import android.net.Uri

data class MediaStoreInfo(
    val count: Int = 0,
    val folderUri: Uri? = null,
) {
    val isEmpty: Boolean
        get() = this == MediaStoreInfo()

    val isNotEmpty: Boolean
        get() = !isEmpty
}
