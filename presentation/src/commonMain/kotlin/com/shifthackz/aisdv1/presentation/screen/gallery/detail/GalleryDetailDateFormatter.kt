package com.shifthackz.aisdv1.presentation.screen.gallery.detail

/**
 * Executes the `formatGalleryCreatedAt` step in the SDAI presentation layer.
 *
 * @param epochMillis epoch millis value consumed by the API.
 * @return Result produced by `formatGalleryCreatedAt`.
 * @author Dmitriy Moroz
 */
internal expect fun formatGalleryCreatedAt(epochMillis: Long): String
