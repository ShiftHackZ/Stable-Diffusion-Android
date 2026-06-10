package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionLoraContract

/**
 * Carries `StableDiffusionLoraEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = StableDiffusionLoraContract.TABLE)
data class StableDiffusionLoraEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionLoraContract.ID)
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionLoraContract.NAME)
    val name: String,
    /**
     * Exposes the `alias` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionLoraContract.ALIAS)
    val alias: String,
    /**
     * Exposes the `path` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionLoraContract.PATH)
    val path: String,
)
