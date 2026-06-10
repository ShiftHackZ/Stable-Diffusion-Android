package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `MediaStoreInfo` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class MediaStoreInfo(
    /**
     * Exposes the `count` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val count: Int = 0,
    /**
     * Exposes the `folderUri` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val folderUri: String? = null,
) {
    val isEmpty: Boolean
        get() = this == MediaStoreInfo()

    val isNotEmpty: Boolean
        get() = !isEmpty
}
