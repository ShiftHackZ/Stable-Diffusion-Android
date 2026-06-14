package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.NetworkUsageContract

/**
 * Persisted byte counter for one Settings network usage bucket.
 *
 * @property category Stable [NetworkUsageContract.CATEGORY] key.
 * @property bytes Accumulated bytes since the last reset.
 * @param category Stable [NetworkUsageContract.CATEGORY] key.
 * @param bytes Accumulated bytes since the last reset.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = NetworkUsageContract.TABLE)
data class NetworkUsageEntity(
    @PrimaryKey
    @ColumnInfo(name = NetworkUsageContract.CATEGORY)
    val category: String,
    @ColumnInfo(name = NetworkUsageContract.BYTES)
    val bytes: Long,
)
