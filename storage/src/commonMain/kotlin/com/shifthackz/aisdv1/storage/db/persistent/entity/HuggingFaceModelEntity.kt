package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.HuggingFaceModelContract

@Entity(tableName = HuggingFaceModelContract.TABLE)
data class HuggingFaceModelEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = HuggingFaceModelContract.ID)
    val id: String,
    @ColumnInfo(name = HuggingFaceModelContract.NAME)
    val name: String,
    @ColumnInfo(name = HuggingFaceModelContract.ALIAS)
    val alias: String,
    @ColumnInfo(name = HuggingFaceModelContract.SOURCE)
    val source: String,
)
