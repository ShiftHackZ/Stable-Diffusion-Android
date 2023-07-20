package com.shifthackz.aisdv1.data.gateway.mediastore

import android.net.Uri
import android.os.Environment
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import java.io.File

/**
 * Implementation to support old Android versions (12 and lower).
 *
 *
 * Working on:
 * - Android 9 API 28 (Emulator)
 * - Android 10 API 29 (Emulator)
 * - Android 11 API 30 (Emulator)
 * - Android 12 API 31 (Emulator)
 *
 * Not working on:
 * - Android 12L API 32 (Emulator)
 */
@Deprecated("Deprecated since Android 12, it is here to support old devices.")
internal class MediaStoreGatewayOldImpl : MediaStoreGateway {

    override fun exportToFile(fileName: String, content: ByteArray) {
        val dirPath = Environment.getExternalStorageDirectory().path + DIR_PATH
        val dir = File(dirPath)
        if (!dir.exists()) dir.mkdirs()
        val file = File("${dirPath}/${fileName}.jpg")
        if (!file.exists()) file.createNewFile()
        file.writeBytes(content)
    }

    override fun getInfo(): MediaStoreInfo {
        val dirPath = Environment.getExternalStorageDirectory().path + DIR_PATH
        val dir = File(dirPath)
        if (dir.exists() && dir.isDirectory) {
            return MediaStoreInfo(
                count = dir.listFiles()?.size ?: 0,
                folderUri = Uri.fromFile(dir),
            )
        }
        return MediaStoreInfo()
    }

    companion object {
        private const val DIR_PATH = "/Download/SDAI"
    }
}
