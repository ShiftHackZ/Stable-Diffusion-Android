package com.shifthackz.aisdv1.data.gateway.mediastore

import android.content.Context
import android.os.Build
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class MediaStoreGatewayFactoryTest {

    private val stubContext = mockk<Context>()
    private val stubFileProviderDescriptor = mockk<FileProviderDescriptor>()

    @Test
    fun `given app running on Android SDK 26 (O), expected factory returned instance of type MediaStoreGatewayOldImpl`() {
        val factory = factory(Build.VERSION_CODES.O)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayOldImpl)
    }

    @Test
    fun `given app running on Android SDK 31 (S), expected factory returned instance of type MediaStoreGatewayOldImpl`() {
        val factory = factory(Build.VERSION_CODES.S)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayOldImpl)
    }

    @Test
    fun `given app running on Android SDK 32 (S_V2), expected factory returned instance of type MediaStoreGatewayImpl`() {
        val factory = factory(Build.VERSION_CODES.S_V2)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayImpl)
    }

    @Test
    fun `given app running on Android SDK 34 (UPSIDE_DOWN_CAKE), expected factory returned instance of type MediaStoreGatewayImpl`() {
        val factory = factory(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayImpl)
    }

    private fun factory(sdkInt: Int) = MediaStoreGatewayFactory(
        context = stubContext,
        fileProviderDescriptor = stubFileProviderDescriptor,
        shouldUseNewMediaStore = { sdkInt >= Build.VERSION_CODES.S_V2 },
    )
}
