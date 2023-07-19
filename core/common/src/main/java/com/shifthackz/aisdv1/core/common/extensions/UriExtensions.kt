package com.shifthackz.aisdv1.core.common.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.FileProvider
import java.io.File

private const val APP_MARKET_PACKAGE = "com.shifthackz.aisdv1.app"

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S_V2)
fun shouldUseNewMediaStore(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2
}

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

fun Context.openMarket() {
    openUri(Uri.parse("market://details?id=$APP_MARKET_PACKAGE"))
}
