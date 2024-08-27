package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.LocalModelContract

@Entity(tableName = LocalModelContract.TABLE)
data class LocalModelEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = LocalModelContract.ID)
    val id: String,
    @ColumnInfo(name = LocalModelContract.TYPE, defaultValue = "onnx")
    val type: String,
    @ColumnInfo(name = LocalModelContract.NAME)
    val name: String,
    @ColumnInfo(name = LocalModelContract.SIZE)
    val size: String,
    @ColumnInfo(name = LocalModelContract.SOURCES)
    val sources: List<String>,
)
