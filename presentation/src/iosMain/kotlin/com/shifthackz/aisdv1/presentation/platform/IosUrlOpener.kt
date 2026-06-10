package com.shifthackz.aisdv1.presentation.platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * Provides the `IosUrlOpener` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal object IosUrlOpener {

    /**
     * Executes the `openUrl` step in the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    fun openUrl(url: String) {
        NSURL.URLWithString(url.sanitizeUrl())?.let { nsUrl ->
            UIApplication.sharedApplication.openURL(
                nsUrl,
                options = emptyMap<Any?, Any?>(),
                completionHandler = null,
            )
        }
    }

    /**
     * Executes the `sanitizeUrl` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private fun String.sanitizeUrl(): String = replace(" ", "%20")
}
