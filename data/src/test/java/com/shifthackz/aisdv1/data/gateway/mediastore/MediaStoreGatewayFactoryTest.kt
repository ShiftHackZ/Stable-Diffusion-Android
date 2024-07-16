package com.shifthackz.aisdv1.data.gateway.mediastore

import android.content.Context
import android.os.Build
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class MediaStoreGatewayFactoryTest {

    private val stubContext = mockk<Context>()
    private val stubFileProviderDescriptor = mockk<FileProviderDescriptor>()

    private val factory = MediaStoreGatewayFactory(
        context = stubContext,
        fileProviderDescriptor = stubFileProviderDescriptor,
    )

    @Test
    fun `given app running on Android SDK 26 (O), expected factory returned instance of type MediaStoreGatewayOldImpl`() {
        mockSdkInt(Build.VERSION_CODES.O)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayOldImpl)
    }

    @Test
    fun `given app running on Android SDK 31 (S), expected factory returned instance of type MediaStoreGatewayOldImpl`() {
        mockSdkInt(Build.VERSION_CODES.S)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayOldImpl)
    }

    @Test
    fun `given app running on Android SDK 32 (S_V2), expected factory returned instance of type MediaStoreGatewayOldImpl`() {
        mockSdkInt(Build.VERSION_CODES.S_V2)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayImpl)
    }

    @Test
    fun `given app running on Android SDK 34 (UPSIDE_DOWN_CAKE), expected factory returned instance of type MediaStoreGatewayOldImpl`() {
        mockSdkInt(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        val actual = factory.invoke()
        Assert.assertEquals(true, actual is MediaStoreGatewayImpl)
    }

    private fun mockSdkInt(sdkInt: Int) {
        val sdkIntField = Build.VERSION::class.java.getField("SDK_INT")
        sdkIntField.isAccessible = true
        getModifiersField().also {
            it.isAccessible = true
            it.set(sdkIntField, sdkIntField.modifiers and Modifier.FINAL.inv())
        }
        sdkIntField.set(null, sdkInt)
    }

    private fun getModifiersField(): Field {
        return try {
            Field::class.java.getDeclaredField("modifiers")
        } catch (e: NoSuchFieldException) {
            try {
                val getDeclaredFields0: Method =
                    Class::class.java.getDeclaredMethod("getDeclaredFields0", Boolean::class.javaPrimitiveType)
                getDeclaredFields0.isAccessible = true
                val fields = getDeclaredFields0.invoke(Field::class.java, false) as Array<Field>
                for (field in fields) {
                    if ("modifiers" == field.name) {
                        return field
                    }
                }
            } catch (ex: ReflectiveOperationException) {
                e.addSuppressed(ex)
            }
            throw e
        }
    }
}
