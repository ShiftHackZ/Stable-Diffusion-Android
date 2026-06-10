package com.shifthackz.aisdv1.presentation.screen.gallery.detail

/**
 * Creates the SDAI value produced by `createDefaultGalleryDetailPlatformActions`.
 *
 * @return Result produced by `createDefaultGalleryDetailPlatformActions`.
 * @author Dmitriy Moroz
 */
actual fun createDefaultGalleryDetailPlatformActions(): GalleryDetailPlatformActions =
    NoOpGalleryDetailPlatformActions
