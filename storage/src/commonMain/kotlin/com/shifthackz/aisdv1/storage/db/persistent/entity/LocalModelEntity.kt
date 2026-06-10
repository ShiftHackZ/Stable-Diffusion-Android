package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract

/**
 * Carries `LocalModelEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = LocalModelContract.TABLE)
data class LocalModelEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = LocalModelContract.ID)
    val id: String,
    /**
     * Exposes the `type` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = LocalModelContract.TYPE, defaultValue = "onnx")
    val type: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = LocalModelContract.NAME)
    val name: String,
    /**
     * Exposes the `size` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = LocalModelContract.SIZE)
    val size: String,
    /**
     * Exposes the `sources` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = LocalModelContract.SOURCES)
    val sources: List<String>,
)
