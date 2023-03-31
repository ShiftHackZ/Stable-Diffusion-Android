package com.shifthackz.aisdv1.storage.db.coins.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.coins.contract.EarnedCoinContract
import java.util.*

@Entity(tableName = EarnedCoinContract.TABLE)
data class EarnedCoinEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = EarnedCoinContract.ID)
    val id: Long,
    @ColumnInfo(name = EarnedCoinContract.DATE)
    val date: Date,
)
