package com.shifthackz.aisdv1.presentation.screen.setup.platform

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.platform.Platform
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.ServerSourceReadiness
import com.shifthackz.aisdv1.presentation.model.displayName
import com.shifthackz.aisdv1.presentation.model.readinessFor
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.allowedModes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowBuild

@RunWith(RobolectricTestRunner::class)
class ServerSetupPlatformTest {

    @After
    fun tearDown() {
        ShadowBuild.reset()
    }

    @Test
    fun `given Android build, expected Core ML excluded by allowed platforms`() {
        assertFalse(ServerSource.LOCAL_APPLE_CORE_ML in TestAndroidBuildInfoProvider.allowedModes)
    }

    @Test
    fun `given Android device has arm64 ABI, expected Bonsai provider visible`() {
        assertTrue(isAndroidBonsaiSupportedInPrinciple(arrayOf("armeabi-v7a", "arm64-v8a")))
    }

    @Test
    fun `given Android device lacks arm64 ABI, expected Bonsai provider hidden`() {
        assertFalse(isAndroidBonsaiSupportedInPrinciple(arrayOf("x86_64")))
    }

    @Test
    fun `given platform reports arm64 ABI, expected Bonsai provider visible in setup list`() {
        ShadowBuild.setSupported64BitAbis(arrayOf("arm64-v8a"))

        assertTrue(isServerSourceAvailableOnPlatform(ServerSource.LOCAL_APPLE_BONSAI))
    }

    @Test
    fun `given Bonsai provider, expected platform specific display name`() {
        assertEquals(
            "Local Diffusion PrismML Bonsai",
            ServerSource.LOCAL_APPLE_BONSAI.displayName(Platform.ANDROID),
        )
        assertEquals(
            "Silicon Diffusion PrismML Bonsai",
            ServerSource.LOCAL_APPLE_BONSAI.displayName(Platform.IOS),
        )
    }

    @Test
    fun `given Bonsai provider, expected platform specific readiness`() {
        assertEquals(
            ServerSourceReadiness.EXPERIMENTAL,
            ServerSource.LOCAL_APPLE_BONSAI.readinessFor(Platform.ANDROID),
        )
        assertEquals(
            ServerSourceReadiness.BETA,
            ServerSource.LOCAL_APPLE_BONSAI.readinessFor(Platform.IOS),
        )
    }
}

private object TestAndroidBuildInfoProvider : BuildInfoProvider {
    override val isDebug: Boolean = true
    override val buildNumber: Int = 0
    override val version: BuildVersion = BuildVersion()
    override val type: BuildType = BuildType.FULL
    override val platform: Platform = Platform.ANDROID
}
