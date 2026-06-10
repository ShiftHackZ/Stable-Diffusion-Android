package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.HuggingFaceModelContract

/**
 * Carries `HuggingFaceModelEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = HuggingFaceModelContract.TABLE)
data class HuggingFaceModelEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = HuggingFaceModelContract.ID)
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = HuggingFaceModelContract.NAME)
    val name: String,
    /**
     * Exposes the `alias` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = HuggingFaceModelContract.ALIAS)
    val alias: String,
    /**
     * Exposes the `source` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = HuggingFaceModelContract.SOURCE)
    val source: String,
)
