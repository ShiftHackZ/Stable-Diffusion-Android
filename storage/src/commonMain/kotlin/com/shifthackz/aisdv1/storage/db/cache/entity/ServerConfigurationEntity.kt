package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.ServerConfigurationContract

/**
 * Carries `ServerConfigurationEntity` data through the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
@Entity(tableName = ServerConfigurationContract.TABLE)
data class ServerConfigurationEntity(
    /**
     * Exposes the `serverId` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ServerConfigurationContract.SERVER_ID)
    val serverId: String,
    /**
     * Exposes the `sdModelCheckpoint` value used by the SDAI storage layer.
     *
     * @author Dmitriy Moroz
     */
    @ColumnInfo(name = ServerConfigurationContract.SD_MODEL_CHECKPOINT)
    val sdModelCheckpoint: String,
)
