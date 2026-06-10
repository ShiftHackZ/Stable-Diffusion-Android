package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionHyperNetworkContract

@Entity(tableName = StableDiffusionHyperNetworkContract.TABLE)
data class StableDiffusionHyperNetworkEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionHyperNetworkContract.ID)
    val id: String,
    @ColumnInfo(name = StableDiffusionHyperNetworkContract.NAME)
    val name: String,
    @ColumnInfo(name = StableDiffusionHyperNetworkContract.PATH)
    val path: String,
)
