package com.shifthackz.aisdv1.data.gateway.mediastore

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.shifthackz.aisdv1.core.common.extensions.uriFromFile
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import java.io.File

/**
 * Implementation to support actual Android versions (12L and higher).
 *
 *
 * Works on:
 * - Android 12 API 32 (Emulator)
 * - Android 13 API 33 (Google Pixel 7 Pro, Graphene OS)
 */
internal class MediaStoreGatewayImpl(
    private val context: Context,
    private val fileProviderDescriptor: FileProviderDescriptor,
) : MediaStoreGateway {

    override fun exportToFile(fileName: String, content: ByteArray) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/SDAI/")
        }

        val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")

        // query for the file
        val cursor: Cursor? = context.contentResolver.query(
            extVolumeUri,
            null,
            MediaStore.MediaColumns.DISPLAY_NAME + " = ? AND " + MediaStore.MediaColumns.MIME_TYPE + " = ?",
            arrayOf(fileName, "image/jpeg"),
            null
        )

        var fileUri: Uri? = null

        // if file found
        if (cursor != null && cursor.count > 0) {
            // get URI
            while (cursor.moveToNext()) {
                val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (nameIndex > -1) {
                    val displayName = cursor.getString(nameIndex)
                    if (displayName == fileName) {
                        val idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID)
                        if (idIndex > -1) {
                            val id = cursor.getLong(idIndex)
                            fileUri = ContentUris.withAppendedId(extVolumeUri, id)
                        }
                    }
                }
            }

            cursor.close()
        } else {
            // insert new file otherwise

            fileUri = context.contentResolver.insert(extVolumeUri, contentValues)
        }

        if (fileUri != null) {
            val os = context.contentResolver.openOutputStream(fileUri, "wt")

            if (os != null) {
                os.write(content)
                os.close()
            }
        }
    }

    override fun getInfo(): MediaStoreInfo {
        try {
            val extVolumeUri: Uri = MediaStore.Files.getContentUri("external")
            val cursor = context.contentResolver.query(
                extVolumeUri,
                arrayOf(MediaStore.MediaColumns.DATA),
                null,
                null,
                null,
            )
            if (cursor != null && cursor.count > 0 && cursor.moveToFirst()) {
                val path = cursor.getString(0)
                val file = File(path)
                cursor.close()
                return file.parentFile
                    ?.takeIf(File::exists)
                    ?.takeIf(File::isDirectory)
                    ?.let { dir ->
                        val uri = context.uriFromFile(dir, fileProviderDescriptor.providerPath)
                        MediaStoreInfo(dir.listFiles()?.size ?: 0, uri)
                    }
                    ?: MediaStoreInfo()

            }
            return MediaStoreInfo()
        } catch (e: Exception) {
            return MediaStoreInfo()
        }
    }
}
