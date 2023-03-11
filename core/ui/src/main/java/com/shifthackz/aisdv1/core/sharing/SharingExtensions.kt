package com.shifthackz.aisdv1.core.sharing

import android.content.Context
import android.content.Intent
import com.shifthackz.aisdv1.core.extensions.uriFromFile
import java.io.File

fun Context.shareText(
    text: String,
    shareChooserTitle: String? = null,
) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
}

fun Context.shareFile(
    file: File,
    fileProviderPath: String,
    fileMimeType: String? = null,
    shareChooserTitle: String? = null,
) {
    val uri = uriFromFile(file, fileProviderPath)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        fileMimeType?.let { type = it }
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
}
