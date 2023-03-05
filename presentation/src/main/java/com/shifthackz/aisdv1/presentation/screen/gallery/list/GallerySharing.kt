package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.content.Context
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.sharing.shareFile
import org.koin.java.KoinJavaComponent.inject

import java.io.File

class GallerySharing {

    private val fileProviderDescriptor: FileProviderDescriptor
            by inject(FileProviderDescriptor::class.java)

    operator fun invoke(context: Context, file: File, mimeType: String) = context.shareFile(
        file = file,
        fileProviderPath = fileProviderDescriptor.providerPath,
        fileMimeType = mimeType,
        shareChooserTitle = "Export gallery archive",
    )
}
