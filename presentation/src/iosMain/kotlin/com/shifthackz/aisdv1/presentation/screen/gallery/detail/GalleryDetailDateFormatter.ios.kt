package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import platform.Foundation.NSDate

/**
 * Executes the `formatGalleryCreatedAt` step in the SDAI presentation layer.
 *
 * @param epochMillis epoch millis value consumed by the API.
 * @return Result produced by `formatGalleryCreatedAt`.
 * @author Dmitriy Moroz
 */
internal actual fun formatGalleryCreatedAt(epochMillis: Long): String =
    NSDate(
        timeIntervalSinceReferenceDate = epochMillis.toDouble() / 1000.0 - APPLE_REFERENCE_EPOCH_SECONDS,
    ).toString()

/**
 * Exposes the `APPLE_REFERENCE_EPOCH_SECONDS` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private const val APPLE_REFERENCE_EPOCH_SECONDS = 978307200.0
