package com.shifthackz.aisdv1.presentation.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RootAppRouterTest {

    @Test
    fun `given gallery detail route, expected back pops stack without crash`() {
        val router = RootAppRouter(AppRoute.TextToImage)

        router.navigateToGallery()
        router.navigateToGalleryDetails(42L)
        router.navigateBack()

        assertEquals(AppRoute.Gallery, router.route.value)
        assertTrue(router.canNavigateBack.value)
    }

    @Test
    fun `given secondary home route without stack, expected back returns to text to image`() {
        val router = RootAppRouter(AppRoute.TextToImage)

        router.navigateToSettings()
        router.navigateBack()

        assertEquals(AppRoute.TextToImage, router.route.value)
        assertFalse(router.canNavigateBack.value)
    }

    @Test
    fun `given root route, expected back is no op`() {
        val router = RootAppRouter(AppRoute.TextToImage)

        router.navigateBack()

        assertEquals(AppRoute.TextToImage, router.route.value)
        assertFalse(router.canNavigateBack.value)
    }

    @Test
    fun `given drawer is open, expected back closes drawer only`() {
        val router = RootAppRouter(AppRoute.TextToImage)
        router.openDrawer()

        router.navigateBack()

        assertEquals(AppRoute.TextToImage, router.route.value)
        assertFalse(router.drawerOpen.value)
    }

    @Test
    fun `given gallery detail route, expected replace with image to image keeps gallery behind`() {
        val router = RootAppRouter(AppRoute.TextToImage)

        router.navigateToGallery()
        router.navigateToGalleryDetails(42L)
        router.navigateToImageToImage()

        assertEquals(AppRoute.ImageToImage, router.route.value)

        router.navigateBack()

        assertEquals(AppRoute.Gallery, router.route.value)
    }
}
