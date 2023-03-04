package com.shifthackz.aisdv1.storage.db_cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db_cache.contract.StableDiffusionModelContract

@Entity(tableName = StableDiffusionModelContract.TABLE)
data class StableDiffusionModelEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionModelContract.ID)
    val id: String,
    @ColumnInfo(name = StableDiffusionModelContract.TITLE)
    val title: String,
    @ColumnInfo(name = StableDiffusionModelContract.NAME)
    val name: String,
    @ColumnInfo(name = StableDiffusionModelContract.HASH)
    val hash: String,
    @ColumnInfo(name = StableDiffusionModelContract.SHA256)
    val sha256: String,
    @ColumnInfo(name = StableDiffusionModelContract.FILENAME)
    val filename: String,
    @ColumnInfo(name = StableDiffusionModelContract.CONFIG)
    val config: String,
)
