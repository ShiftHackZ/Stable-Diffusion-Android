package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.GenerationResultContract
import java.util.*

@Entity(tableName = GenerationResultContract.TABLE)
data class GenerationResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = GenerationResultContract.ID)
    val id: Long,
    @ColumnInfo(name = GenerationResultContract.IMAGE_BASE_64)
    val imageBase64: String,
    @ColumnInfo(name = GenerationResultContract.CREATED_AT)
    val cratedAt: Date,
)

