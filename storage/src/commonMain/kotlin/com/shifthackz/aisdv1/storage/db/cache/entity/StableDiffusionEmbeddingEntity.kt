package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionEmbeddingContract

/**
 * Carries `StableDiffusionEmbeddingEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = StableDiffusionEmbeddingContract.TABLE)
data class StableDiffusionEmbeddingEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionEmbeddingContract.ID)
    val id: String,
    /**
     * Exposes the `keyword` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionEmbeddingContract.KEYWORD)
    val keyword: String,
)
