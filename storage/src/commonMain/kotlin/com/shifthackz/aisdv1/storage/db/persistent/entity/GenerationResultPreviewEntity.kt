package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract

/**
 * Carries lightweight gallery-grid projection data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
data class GenerationResultPreviewEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.ID)
    val id: Long,
    /**
     * Exposes the `imageBase64` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.IMAGE_BASE_64)
    val imageBase64: String,
    /**
     * Exposes the `hidden` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.HIDDEN)
    val hidden: Boolean,
    /**
     * Exposes the `liked` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = GenerationResultContract.LIKED)
    val liked: Boolean = false,
)
