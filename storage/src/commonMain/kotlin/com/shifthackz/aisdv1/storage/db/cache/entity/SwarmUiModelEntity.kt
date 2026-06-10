package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.SwarmUiModelContract

@Entity(tableName = SwarmUiModelContract.TABLE)
data class SwarmUiModelEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = SwarmUiModelContract.ID)
    val id: String,
    @ColumnInfo(name = SwarmUiModelContract.NAME)
    val name: String,
    @ColumnInfo(name = SwarmUiModelContract.TITLE)
    val title: String,
    @ColumnInfo(name = SwarmUiModelContract.AUTHOR)
    val author: String,
)
