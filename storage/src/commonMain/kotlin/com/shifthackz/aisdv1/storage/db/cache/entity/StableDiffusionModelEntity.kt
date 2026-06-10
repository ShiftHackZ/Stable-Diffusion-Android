package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionModelContract

/**
 * Carries `StableDiffusionModelEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = StableDiffusionModelContract.TABLE)
data class StableDiffusionModelEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionModelContract.ID)
    val id: String,
    /**
     * Exposes the `title` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionModelContract.TITLE)
    val title: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionModelContract.NAME)
    val name: String,
    /**
     * Exposes the `hash` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionModelContract.HASH)
    val hash: String,
    /**
     * Exposes the `sha256` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionModelContract.SHA256)
    val sha256: String,
    /**
     * Exposes the `filename` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionModelContract.FILENAME)
    val filename: String,
    /**
     * Exposes the `config` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = StableDiffusionModelContract.CONFIG)
    val config: String,
)
