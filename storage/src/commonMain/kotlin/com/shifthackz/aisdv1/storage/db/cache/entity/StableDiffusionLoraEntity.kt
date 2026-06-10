package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionLoraContract

@Entity(tableName = StableDiffusionLoraContract.TABLE)
data class StableDiffusionLoraEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionLoraContract.ID)
    val id: String,
    @ColumnInfo(name = StableDiffusionLoraContract.NAME)
    val name: String,
    @ColumnInfo(name = StableDiffusionLoraContract.ALIAS)
    val alias: String,
    @ColumnInfo(name = StableDiffusionLoraContract.PATH)
    val path: String,
)
