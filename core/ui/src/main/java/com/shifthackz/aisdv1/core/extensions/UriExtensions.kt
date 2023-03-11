package com.shifthackz.aisdv1.core.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.uriFromFile(file: File, fileProviderPath: String): Uri {
    return FileProvider.getUriForFile(this, fileProviderPath, file)
}

fun Context.openUri(uri: Uri) {
    val uriIntent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(uriIntent)
}

fun Context.openUrl(url: String) {
    val uri = Uri.parse(url)
    openUri(uri)
}
