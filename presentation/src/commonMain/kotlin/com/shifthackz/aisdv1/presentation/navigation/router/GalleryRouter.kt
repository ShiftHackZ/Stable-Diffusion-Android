package com.shifthackz.aisdv1.presentation.navigation.router

interface GalleryRouter {
    fun openDrawer()
    fun navigateToGalleryDetails(itemId: Long)
}

object NoOpGalleryRouter : GalleryRouter {
    override fun openDrawer() = Unit
    override fun navigateToGalleryDetails(itemId: Long) = Unit
}
