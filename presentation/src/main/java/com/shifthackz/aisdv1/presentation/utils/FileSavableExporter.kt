package com.shifthackz.aisdv1.presentation.utils

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.file.writeBitmap
import com.shifthackz.aisdv1.core.common.file.writeFilesToZip
import io.reactivex.rxjava3.core.Single
import java.io.File

sealed interface FileSavableExporter {

    val fileProviderDescriptor: FileProviderDescriptor

    interface BmpToFile : FileSavableExporter {
        fun saveBitmapToFile(fileName: String, bitmap: Bitmap) = Single.create { emitter ->
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "$fileName.jpg")
            outFile.writeBitmap(bitmap)
            emitter.onSuccess(outFile)
        }
    }

    interface FilesToZip : FileSavableExporter {
        fun saveFilesToZip(files: List<File>) = Single.create { emitter ->
            val cacheDirectory = File(fileProviderDescriptor.imagesCacheDirPath)
            if (!cacheDirectory.exists()) cacheDirectory.mkdirs()
            val outFile = File(cacheDirectory, "export_${System.currentTimeMillis()}.zip")
            outFile.writeFilesToZip(files)
            emitter.onSuccess(outFile)
        }
    }
}
