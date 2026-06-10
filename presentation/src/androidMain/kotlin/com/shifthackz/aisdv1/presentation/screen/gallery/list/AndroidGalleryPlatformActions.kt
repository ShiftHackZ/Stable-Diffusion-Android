package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.sharing.shareFile
import java.io.File

/**
 * Coordinates `AndroidGalleryPlatformActions` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidGalleryPlatformActions(
    /**
     * Exposes the `context` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val context: Context,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
) : GalleryPlatformActions {

    /**
     * Executes the `openMediaStoreFolder` step in the SDAI presentation layer.
     *
     * @param folderUri folder uri value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun openMediaStoreFolder(folderUri: String) {
        with(Intent(Intent.ACTION_VIEW)) {
            setDataAndType(Uri.parse(folderUri), DocumentsContract.Document.MIME_TYPE_DIR)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(this)
        }
    }

    /**
     * Performs the SDAI side effect handled by `shareExport`.
     *
     * @param filePath file path value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun shareExport(filePath: String) {
        context.shareFile(
            file = File(filePath),
            fileProviderPath = fileProviderDescriptor.providerPath,
            fileMimeType = MIME_TYPE_ZIP,
        )
    }

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `MIME_TYPE_ZIP` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        const val MIME_TYPE_ZIP = "application/zip"
    }
}
