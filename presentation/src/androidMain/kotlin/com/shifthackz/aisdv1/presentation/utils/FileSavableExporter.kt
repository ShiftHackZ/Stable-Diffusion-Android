package com.shifthackz.aisdv1.presentation.utils

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.writeBitmap
import com.shifthackz.aisdv1.core.common.file.writeFilesToZip
import java.io.File

sealed interface FileSavableExporter {

    val fileProviderDescriptor: FileProviderDescriptor

    interface BmpToFile : FileSavableExporter {
        fun saveBitmapToFile(fileName: String, bitmap: Bitmap): File {
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "$fileName.jpg")
            outFile.writeBitmap(bitmap)
            return outFile
        }
    }

    interface FilesToZip : FileSavableExporter {
        fun saveFilesToZip(files: List<File>): File {
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "export_${System.currentTimeMillis()}.zip")
            outFile.writeFilesToZip(files)
            return outFile
        }
    }
}
