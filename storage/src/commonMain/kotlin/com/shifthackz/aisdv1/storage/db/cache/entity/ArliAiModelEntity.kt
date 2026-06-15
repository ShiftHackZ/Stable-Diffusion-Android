package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.ArliAiModelContract

/**
 * Stores one ArliAI checkpoint in the local cache database.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = ArliAiModelContract.TABLE)
data class ArliAiModelEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ArliAiModelContract.ID)
    val id: String,
    /**
     * Exposes the `title` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ArliAiModelContract.TITLE)
    val title: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ArliAiModelContract.NAME)
    val name: String,
    /**
     * Exposes the `hash` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ArliAiModelContract.HASH)
    val hash: String,
    /**
     * Exposes the `sha256` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ArliAiModelContract.SHA256)
    val sha256: String,
    /**
     * Exposes the `filename` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ArliAiModelContract.FILENAME)
    val filename: String,
    /**
     * Exposes the `config` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ArliAiModelContract.CONFIG)
    val config: String,
)
