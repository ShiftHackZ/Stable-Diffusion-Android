package com.shifthackz.aisdv1.presentation.navigation.router

interface GalleryDetailRouter {
    fun navigateBack()
    fun navigateToTextToImage()
    fun navigateToImageToImage()
    fun navigateToReportImage(itemId: Long)
}

object NoOpGalleryDetailRouter : GalleryDetailRouter {
    override fun navigateBack() = Unit
    override fun navigateToTextToImage() = Unit
    override fun navigateToImageToImage() = Unit
    override fun navigateToReportImage(itemId: Long) = Unit
}
