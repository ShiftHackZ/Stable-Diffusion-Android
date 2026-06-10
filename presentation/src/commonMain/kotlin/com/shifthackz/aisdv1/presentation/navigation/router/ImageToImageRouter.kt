package com.shifthackz.aisdv1.presentation.navigation.router

interface ImageToImageRouter {
    fun openDrawer()
    fun navigateBack()
    fun navigateToServerSetup()
    fun navigateToImageInPaint()
    fun navigateToGalleryDetails(itemId: Long)
    fun navigateToReportImage(itemId: Long)
}

data object NoOpImageToImageRouter : ImageToImageRouter {
    override fun openDrawer() = Unit
    override fun navigateBack() = Unit
    override fun navigateToServerSetup() = Unit
    override fun navigateToImageInPaint() = Unit
    override fun navigateToGalleryDetails(itemId: Long) = Unit
    override fun navigateToReportImage(itemId: Long) = Unit
}
