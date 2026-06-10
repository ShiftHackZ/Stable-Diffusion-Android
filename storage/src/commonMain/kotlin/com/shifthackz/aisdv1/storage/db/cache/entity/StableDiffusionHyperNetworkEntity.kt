package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionHyperNetworkContract

/**
 * Carries `StableDiffusionHyperNetworkEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = StableDiffusionHyperNetworkContract.TABLE)
data class StableDiffusionHyperNetworkEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionHyperNetworkContract.ID)
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionHyperNetworkContract.NAME)
    val name: String,
    /**
     * Exposes the `path` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionHyperNetworkContract.PATH)
    val path: String,
)
