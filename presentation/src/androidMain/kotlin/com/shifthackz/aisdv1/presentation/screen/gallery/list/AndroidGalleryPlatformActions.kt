package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.sharing.shareFile
import java.io.File

internal class AndroidGalleryPlatformActions(
    private val context: Context,
    private val fileProviderDescriptor: FileProviderDescriptor,
) : GalleryPlatformActions {

    override fun openMediaStoreFolder(folderUri: String) {
        with(Intent(Intent.ACTION_VIEW)) {
            setDataAndType(Uri.parse(folderUri), DocumentsContract.Document.MIME_TYPE_DIR)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(this)
        }
    }

    override fun shareExport(filePath: String) {
        context.shareFile(
            file = File(filePath),
            fileProviderPath = fileProviderDescriptor.providerPath,
            fileMimeType = MIME_TYPE_ZIP,
        )
    }

    private companion object {
        const val MIME_TYPE_ZIP = "application/zip"
    }
}
