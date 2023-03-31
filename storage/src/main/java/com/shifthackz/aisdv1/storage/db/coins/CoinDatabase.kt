package com.shifthackz.aisdv1.storage.db.coins

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shifthackz.aisdv1.storage.converters.DateConverters
import com.shifthackz.aisdv1.storage.db.coins.CoinDatabase.Companion.DB_VERSION
import com.shifthackz.aisdv1.storage.db.coins.dao.CoinDao
import com.shifthackz.aisdv1.storage.db.coins.dao.EarnedCoinDao
import com.shifthackz.aisdv1.storage.db.coins.entity.CoinEntity
import com.shifthackz.aisdv1.storage.db.coins.entity.EarnedCoinEntity

@Database(
    version = DB_VERSION,
    exportSchema = true,
    entities = [
        CoinEntity::class,
        EarnedCoinEntity::class,
    ],
)
@TypeConverters(DateConverters::class)
internal abstract class CoinDatabase : RoomDatabase() {
    abstract fun coinDao(): CoinDao
    abstract fun earnedCoinDao(): EarnedCoinDao

    companion object {
        const val DB_NAME = "ai_sd_v1_coins_db"
        const val DB_VERSION = 2
    }
}
