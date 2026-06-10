package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import platform.Foundation.NSDate

internal actual fun formatGalleryCreatedAt(epochMillis: Long): String =
    NSDate(
        timeIntervalSinceReferenceDate = epochMillis.toDouble() / 1000.0 - APPLE_REFERENCE_EPOCH_SECONDS,
    ).toString()

private const val APPLE_REFERENCE_EPOCH_SECONDS = 978307200.0
