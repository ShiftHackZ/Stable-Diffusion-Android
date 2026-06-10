package com.shifthackz.aisdv1.storage.di

import androidx.room.Room
import androidx.room.RoomDatabase
import com.shifthackz.aisdv1.storage.db.persistent.PersistentDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

/**
 * Exposes the `persistentDatabasePlatformModule` value used by the SDAI storage layer.
 *
 * @author Dmitriy Moroz
 */
actual val persistentDatabasePlatformModule = module {
    single<RoomDatabase.Builder<PersistentDatabase>> {
        Room.databaseBuilder<PersistentDatabase>(
            name = "${applicationSupportDirectory()}/${PersistentDatabase.DB_NAME}",
        )
    }
}

/**
 * Executes the `applicationSupportDirectory` step in the SDAI storage layer.
 *
 * @return Result produced by `applicationSupportDirectory`.
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
private fun applicationSupportDirectory(): String {
    val directory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    return requireNotNull(directory?.path)
}
