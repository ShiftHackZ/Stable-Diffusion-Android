package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.SwarmUiModelContract

/**
 * Carries `SwarmUiModelEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = SwarmUiModelContract.TABLE)
data class SwarmUiModelEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = SwarmUiModelContract.ID)
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = SwarmUiModelContract.NAME)
    val name: String,
    /**
     * Exposes the `title` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = SwarmUiModelContract.TITLE)
    val title: String,
    /**
     * Exposes the `author` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = SwarmUiModelContract.AUTHOR)
    val author: String,
)
