package com.shifthackz.aisdv1.core.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication

/**
 * Executes the `dismissPlatformKeyboard` step in the SDAI core UI layer.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalForeignApi::class)
internal actual fun dismissPlatformKeyboard() {
    UIApplication.sharedApplication.sendAction(
        action = NSSelectorFromString("resignFirstResponder"),
        to = null,
        from = null,
        forEvent = null,
    )
}
