package com.shifthackz.aisdv1.presentation.screen.gallery.list

interface GalleryPlatformActions {
    fun openMediaStoreFolder(folderUri: String)
    fun shareExport(filePath: String)
}

object NoOpGalleryPlatformActions : GalleryPlatformActions {
    override fun openMediaStoreFolder(folderUri: String) = Unit
    override fun shareExport(filePath: String) = Unit
}
