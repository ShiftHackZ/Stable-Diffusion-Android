package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionSamplerContract

/**
 * Carries `StableDiffusionSamplerEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = StableDiffusionSamplerContract.TABLE)
data class StableDiffusionSamplerEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionSamplerContract.ID)
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionSamplerContract.NAME)
    val name: String,
    /**
     * Exposes the `aliases` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionSamplerContract.ALIASES)
    val aliases: List<String>,
    /**
     * Exposes the `options` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionSamplerContract.OPTIONS)
    val options: Map<String, String>,
)
