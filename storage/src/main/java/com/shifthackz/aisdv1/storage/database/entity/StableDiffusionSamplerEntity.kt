package com.shifthackz.aisdv1.storage.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.database.contract.StableDiffusionSamplerContract

@Entity(tableName = StableDiffusionSamplerContract.TABLE)
data class StableDiffusionSamplerEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionSamplerContract.ID)
    val id: String,
    @ColumnInfo(name = StableDiffusionSamplerContract.NAME)
    val name: String,
    @ColumnInfo(name = StableDiffusionSamplerContract.ALIASES)
    val aliases: List<String>,
    @ColumnInfo(name = StableDiffusionSamplerContract.OPTIONS)
    val options: Map<String, String>,
)
