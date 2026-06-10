package com.shifthackz.aisdv1.core.extensions

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
internal actual fun dismissPlatformKeyboard() {
    UIApplication.sharedApplication.sendAction(
        action = NSSelectorFromString("resignFirstResponder"),
        to = null,
        from = null,
        forEvent = null,
    )
}
