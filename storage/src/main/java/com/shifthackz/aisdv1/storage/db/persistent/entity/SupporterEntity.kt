package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.SupporterContract
import java.util.Date

@Entity(tableName = SupporterContract.TABLE)
data class SupporterEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = SupporterContract.ID)
    val id: Int,
    @ColumnInfo(name = SupporterContract.NAME)
    val name: String,
    @ColumnInfo(name = SupporterContract.DATE)
    val date: Date,
    @ColumnInfo(name = SupporterContract.MESSAGE)
    val message: String,
)
