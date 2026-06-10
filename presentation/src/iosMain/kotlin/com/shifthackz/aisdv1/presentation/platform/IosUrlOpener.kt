package com.shifthackz.aisdv1.presentation.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal object IosUrlOpener {

    fun openUrl(url: String) {
        NSURL.URLWithString(url.sanitizeUrl())?.let { nsUrl ->
            UIApplication.sharedApplication.openURL(
                nsUrl,
                options = emptyMap<Any?, Any?>(),
                completionHandler = null,
            )
        }
    }

    private fun String.sanitizeUrl(): String = replace(" ", "%20")
}
