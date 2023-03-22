package com.shifthackz.aisdv1.presentation.features

import android.content.Context
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.sharing.shareFile
import java.io.File

interface FileSharingFeature {

    val fileProviderDescriptor: FileProviderDescriptor

    fun shareFile(context: Context, file: File, mimeType: String) = context.shareFile(
        file = file,
        fileProviderPath = fileProviderDescriptor.providerPath,
        fileMimeType = mimeType,
        shareChooserTitle = "Export gallery archive",
    )
}
