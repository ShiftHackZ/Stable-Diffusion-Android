package com.shifthackz.aisdv1.presentation.utils

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.writeBitmap
import com.shifthackz.aisdv1.core.common.file.writeFilesToZip
import java.io.File

/**
 * Defines the `FileSavableExporter` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface FileSavableExporter {

    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val fileProviderDescriptor: FileProviderDescriptor

    /**
     * Defines the `BmpToFile` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    interface BmpToFile : FileSavableExporter {
        /**
         * Converts SDAI data with `saveBitmapToFile`.
         *
         * @param fileName file name value consumed by the API.
         * @param bitmap bitmap image processed by the operation.
         * @return Result produced by `saveBitmapToFile`.
         * @author Dmitriy Moroz
         */
        fun saveBitmapToFile(fileName: String, bitmap: Bitmap): File {
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "$fileName.jpg")
            outFile.writeBitmap(bitmap)
            return outFile
        }
    }

    /**
     * Defines the `FilesToZip` contract for the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    interface FilesToZip : FileSavableExporter {
        /**
         * Performs the SDAI side effect handled by `saveFilesToZip`.
         *
         * @param files files used by the operation.
         * @return Result produced by `saveFilesToZip`.
         * @author Dmitriy Moroz
         */
        fun saveFilesToZip(files: List<File>): File {
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "export_${System.currentTimeMillis()}.zip")
            outFile.writeFilesToZip(files)
            return outFile
        }
    }
}
