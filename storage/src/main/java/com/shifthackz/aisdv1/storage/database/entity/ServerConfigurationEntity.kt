package com.shifthackz.aisdv1.storage.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.database.contract.ServerConfigurationContract

@Entity(tableName = ServerConfigurationContract.TABLE)
data class ServerConfigurationEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = ServerConfigurationContract.SERVER_ID)
    val serverId: String,
    @ColumnInfo(name = ServerConfigurationContract.SD_MODEL_CHECKPOINT)
    val sdModelCheckpoint: String,
)
