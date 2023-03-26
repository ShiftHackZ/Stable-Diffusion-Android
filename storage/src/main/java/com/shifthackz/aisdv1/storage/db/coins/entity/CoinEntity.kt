package com.shifthackz.aisdv1.storage.db.coins.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.coins.contract.CoinContract
import java.util.*

@Entity(tableName = CoinContract.TABLE)
data class CoinEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CoinContract.ID)
    val id: Long,
    @ColumnInfo(name = CoinContract.DATE)
    val date: Date,
)
