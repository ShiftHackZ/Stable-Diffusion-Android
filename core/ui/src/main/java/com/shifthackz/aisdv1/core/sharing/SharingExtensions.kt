package com.shifthackz.aisdv1.core.sharing

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.shifthackz.aisdv1.core.common.extensions.uriFromFile
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

fun Context.shareEmail(
    email: String,
    subject: String,
    body: String = "",
    file: File? = null,
    fileProviderPath: String = "",
) {
    val uri: Uri? = file?.let { uriFromFile(it, fileProviderPath) }
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        uri?.let { putExtra(Intent.EXTRA_STREAM, it) }
    }
    startActivity(Intent.createChooser(emailIntent, null))
}
