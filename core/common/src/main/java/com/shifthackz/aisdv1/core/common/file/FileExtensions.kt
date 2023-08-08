package com.shifthackz.aisdv1.core.common.file

import android.graphics.Bitmap
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

fun File.writeBitmap(
    bitmap: Bitmap,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    quality: Int = 100,
) {
    outputStream().use { out ->
        bitmap.compress(format, quality, out)
        out.flush()
    }
}

fun File.writeFilesToZip(
    files: List<File>,
    bufferSize: Int = 1024,
) {
    outputStream().use { out ->
        ZipOutputStream(out).use { zipOut ->
            val buffer = ByteArray(bufferSize)
            files.forEach { file ->
                val zipEntry = ZipEntry(
                    file.path.substring(file.path.lastIndexOf("/") + 1)
                )
                zipOut.putNextEntry(zipEntry)
                BufferedInputStream(file.inputStream(), bufferSize).use { fileInput ->
                    var count: Int
                    while (fileInput.read(buffer, 0, bufferSize).also { count = it } != -1) {
                        zipOut.write(buffer, 0, count)
                    }
                    fileInput.close()
                }
            }
            zipOut.close()
        }
    }
}

fun File.unzip() {
    if (!path.endsWith(".zip")) return
    val destinationDir = parentFile ?: return

    fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(DEFAULT_BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

    ZipFile(this).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { inputStream ->
                val filePath = destinationDir.path + File.separator + entry.name
                if (!entry.isDirectory) {
                    extractFile(inputStream, filePath)
                } else {
                    val dir = File(filePath)
                    dir.mkdir()
                }
            }
        }
    }
}
