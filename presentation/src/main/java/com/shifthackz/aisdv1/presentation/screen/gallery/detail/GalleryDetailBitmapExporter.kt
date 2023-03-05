package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.presentation.utils.FileSavableExporter
import io.reactivex.rxjava3.core.Single
import java.io.File

class GalleryDetailBitmapExporter(
    override val fileProviderDescriptor: FileProviderDescriptor,
) : FileSavableExporter.BmpToFile {

    operator fun invoke(bitmap: Bitmap): Single<File> = saveBitmapToFile(
        System.currentTimeMillis().toString(),
        bitmap
    )
}
