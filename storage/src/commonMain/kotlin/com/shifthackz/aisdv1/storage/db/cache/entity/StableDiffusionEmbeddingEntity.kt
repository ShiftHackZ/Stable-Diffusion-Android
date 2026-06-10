package com.shifthackz.aisdv1.storage.db.cache.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.cache.contract.StableDiffusionEmbeddingContract

@Entity(tableName = StableDiffusionEmbeddingContract.TABLE)
data class StableDiffusionEmbeddingEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = StableDiffusionEmbeddingContract.ID)
    val id: String,
    @ColumnInfo(name = StableDiffusionEmbeddingContract.KEYWORD)
    val keyword: String,
)
