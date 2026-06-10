package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.SupporterContract

/**
 * Carries `SupporterEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = SupporterContract.TABLE)
data class SupporterEntity(
    /**
     * Exposes the `id` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = SupporterContract.ID)
    val id: Int,
    /**
     * Exposes the `name` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = SupporterContract.NAME)
    val name: String,
    /**
     * Exposes the `date` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = SupporterContract.DATE)
    val date: Long,
    /**
     * Exposes the `message` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = SupporterContract.MESSAGE)
    val message: String,
)
