package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import com.shifthackz.aisdv1.storage.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, AppDatabase.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().sdModelDao() }
    single { get<AppDatabase>().sdSamplerDao() }
    single { get<AppDatabase>().serverConfigurationDao() }
}
