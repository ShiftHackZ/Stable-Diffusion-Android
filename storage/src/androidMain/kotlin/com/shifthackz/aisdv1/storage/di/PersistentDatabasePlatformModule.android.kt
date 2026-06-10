package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val persistentDatabasePlatformModule = module {
    single<RoomDatabase.Builder<PersistentDatabase>> {
        val context = androidContext().applicationContext
        Room.databaseBuilder<PersistentDatabase>(
            context = context,
            name = context.getDatabasePath(PersistentDatabase.DB_NAME).absolutePath,
        )
    }
}
