package com.shifthackz.aisdv1.core.sharing

import android.app.Activity
import android.content.ClipData
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
    startChooserActivity(shareIntent, shareChooserTitle)
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
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        clipData = ClipData.newUri(contentResolver, file.name, uri)
        putExtra(Intent.EXTRA_STREAM, uri)
    }
    startChooserActivity(shareIntent, shareChooserTitle)
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
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        uri?.let {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newUri(contentResolver, file?.name ?: "attachment", it)
            putExtra(Intent.EXTRA_STREAM, it)
        }
    }
    startChooserActivity(emailIntent, null)
}

private fun Context.startChooserActivity(
    intent: Intent,
    title: String?,
) {
    val chooser = Intent.createChooser(intent, title).apply {
        if (this@startChooserActivity !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if ((intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION) != 0) {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    startActivity(chooser)
}
