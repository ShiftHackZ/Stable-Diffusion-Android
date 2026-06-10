package com.shifthackz.aisdv1.presentation.navigation.router

interface TextToImageRouter {
    fun openDrawer()
    fun navigateBack()
    fun navigateToServerSetup()
    fun navigateToGalleryDetails(itemId: Long)
    fun navigateToReportImage(itemId: Long)
}

data object NoOpTextToImageRouter : TextToImageRouter {
    override fun openDrawer() = Unit
    override fun navigateBack() = Unit
    override fun navigateToServerSetup() = Unit
    override fun navigateToGalleryDetails(itemId: Long) = Unit
    override fun navigateToReportImage(itemId: Long) = Unit
}
