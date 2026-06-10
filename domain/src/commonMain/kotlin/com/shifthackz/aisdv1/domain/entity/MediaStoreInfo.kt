package com.shifthackz.aisdv1.domain.entity

data class MediaStoreInfo(
    val count: Int = 0,
    val folderUri: String? = null,
) {
    val isEmpty: Boolean
        get() = this == MediaStoreInfo()

    val isNotEmpty: Boolean
        get() = !isEmpty
}
