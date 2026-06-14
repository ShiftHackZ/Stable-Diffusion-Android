package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BenchmarkRecommendationPolicyTest {

    @Test
    fun `given pixel 3a class device, expected conservative provider recommendations`() {
        val recommendations = BenchmarkRecommendationPolicy.providerRecommendations(
            deviceInfo = pixel3a(),
            totalScore = 1_200,
            cpuScore = 1_000,
            memoryScore = 900,
            acceleratorScore = 1_100,
        )

        val onnx = recommendations.provider(ServerSource.LOCAL_MICROSOFT_ONNX)
        assertTrue(onnx.recommended)
        assertEquals(256, onnx.width)
        assertEquals(256, onnx.height)
        assertEquals(8, onnx.samplingSteps)
        assertEquals(270, onnx.estimatedTimeSeconds)
        assertTrue(BenchmarkProviderIssue.ONNX_SLOW_LOW_MEMORY in onnx.issues)

        val mediaPipe = recommendations.provider(ServerSource.LOCAL_GOOGLE_MEDIA_PIPE)
        assertFalse(mediaPipe.recommended)
        assertTrue(BenchmarkProviderIssue.MEDIAPIPE_UNSTABLE_LOW_MEMORY in mediaPipe.issues)

        val sdxl = recommendations.provider(ServerSource.LOCAL_STABLE_DIFFUSION_CPP)
        assertFalse(sdxl.recommended)
        assertEquals(SdxlBackend.CPU, sdxl.sdxlBackend)
        assertTrue(BenchmarkProviderIssue.SDXL_TINY_EXPERIMENTAL_ONLY in sdxl.issues)

        val fallback = BenchmarkRecommendationPolicy.fallbackRecommendation(recommendations)
        assertEquals(listOf(ServerSource.LOCAL_MICROSOFT_ONNX), fallback.providers)
        assertEquals(256, fallback.width)
        assertEquals(SdxlBackend.CPU, fallback.sdxlBackend)
    }

    @Test
    fun `given strong android device with api-only acceleration, expected sdxl avoids hardware backend`() {
        val recommendations = BenchmarkRecommendationPolicy.providerRecommendations(
            deviceInfo = strongAndroid(),
            totalScore = 4_400,
            cpuScore = 3_400,
            memoryScore = 3_200,
            acceleratorScore = 1_100,
        )

        val sdxl = recommendations.provider(ServerSource.LOCAL_STABLE_DIFFUSION_CPP)
        assertTrue(sdxl.recommended)
        assertEquals(SdxlBackend.CPU, sdxl.sdxlBackend)
        assertTrue(BenchmarkProviderIssue.SDXL_BACKEND_NOT_VALIDATED in sdxl.issues)
    }

    @Test
    fun `given pixel 3a class device, expected acceleration APIs are not treated as supported backends`() {
        val capabilities = pixel3a().accelerationCapabilities()

        assertEquals(
            BenchmarkAccelerationStatus.BACKEND_UNAVAILABLE,
            capabilities.status(BenchmarkAccelerator.VULKAN),
        )
        assertEquals(
            BenchmarkAccelerationStatus.BACKEND_UNAVAILABLE,
            capabilities.status(BenchmarkAccelerator.OPEN_CL),
        )
        assertEquals(
            BenchmarkAccelerationStatus.NOT_RECOMMENDED,
            capabilities.status(BenchmarkAccelerator.NNAPI),
        )
    }

    @Test
    fun `given high score ios device, expected core ml recommendation`() {
        val recommendations = BenchmarkRecommendationPolicy.providerRecommendations(
            deviceInfo = iphone(),
            totalScore = 7_200,
            cpuScore = 5_800,
            memoryScore = 5_200,
            acceleratorScore = 3_600,
        )

        val coreMl = recommendations.provider(ServerSource.LOCAL_APPLE_CORE_ML)
        assertTrue(coreMl.recommended)
        assertEquals(512, coreMl.width)
        assertEquals(512, coreMl.height)
        assertEquals(50, coreMl.samplingSteps)
        assertEquals(45, coreMl.estimatedTimeSeconds)
        assertTrue(coreMl.backgroundGeneration)
    }

    @Test
    fun `given real iphone 17 pro class device, expected high end core ml recommendation`() {
        val recommendations = BenchmarkRecommendationPolicy.providerRecommendations(
            deviceInfo = iphone17Pro(),
            totalScore = 3_428,
            cpuScore = 2_300,
            memoryScore = 2_100,
            acceleratorScore = 3_580,
        )

        val coreMl = recommendations.provider(ServerSource.LOCAL_APPLE_CORE_ML)
        assertTrue(coreMl.recommended)
        assertEquals(512, coreMl.width)
        assertEquals(512, coreMl.height)
        assertEquals(50, coreMl.samplingSteps)
        assertEquals(45, coreMl.estimatedTimeSeconds)
        assertTrue(coreMl.backgroundGeneration)
    }

    private fun List<BenchmarkProviderRecommendation>.provider(
        source: ServerSource,
    ): BenchmarkProviderRecommendation =
        first { it.provider == source }

    private fun List<BenchmarkAccelerationCapability>.status(
        accelerator: BenchmarkAccelerator,
    ): BenchmarkAccelerationStatus =
        first { it.accelerator == accelerator }.status

    private fun pixel3a() = BenchmarkDeviceInfo(
        platform = BenchmarkPlatform.ANDROID,
        manufacturer = "Google",
        model = "Pixel 3a XL",
        osVersion = "Android 12",
        cpuName = "Qualcomm Technologies, Inc SDM670",
        cpuCores = 8,
        gpuName = "sargo / Adreno 615",
        totalRamMb = 3_580,
        availableRamMb = 1_100,
        totalVramMb = null,
        availableVramMb = null,
        accelerators = listOf(
            BenchmarkAccelerator.VULKAN,
            BenchmarkAccelerator.OPEN_CL,
            BenchmarkAccelerator.NNAPI,
        ),
    )

    private fun strongAndroid() = BenchmarkDeviceInfo(
        platform = BenchmarkPlatform.ANDROID,
        manufacturer = "Google",
        model = "Pixel 10 Pro",
        osVersion = "Android 16",
        cpuName = "Tensor G6",
        cpuCores = 10,
        gpuName = "Mali-GPU",
        totalRamMb = 12_288,
        availableRamMb = 8_000,
        totalVramMb = null,
        availableVramMb = null,
        accelerators = listOf(
            BenchmarkAccelerator.VULKAN,
            BenchmarkAccelerator.OPEN_CL,
            BenchmarkAccelerator.NNAPI,
        ),
    )

    private fun iphone() = BenchmarkDeviceInfo(
        platform = BenchmarkPlatform.IOS,
        manufacturer = "Apple",
        model = "iPhone 17 Pro",
        osVersion = "iOS 20",
        cpuName = "Apple Silicon",
        cpuCores = 8,
        gpuName = "Apple GPU",
        totalRamMb = 8_192,
        availableRamMb = 6_000,
        totalVramMb = null,
        availableVramMb = null,
        accelerators = listOf(
            BenchmarkAccelerator.METAL,
            BenchmarkAccelerator.CORE_ML,
            BenchmarkAccelerator.NEURAL_ENGINE,
        ),
    )

    private fun iphone17Pro() = BenchmarkDeviceInfo(
        platform = BenchmarkPlatform.IOS,
        manufacturer = "Apple",
        model = "iPhone 17 Pro",
        osVersion = "iOS 20",
        cpuName = "Apple A19 Pro",
        cpuCores = 8,
        gpuName = "Apple A19 Pro GPU",
        totalRamMb = 11_721,
        availableRamMb = 8_000,
        totalVramMb = null,
        availableVramMb = null,
        accelerators = listOf(
            BenchmarkAccelerator.METAL,
            BenchmarkAccelerator.CORE_ML,
            BenchmarkAccelerator.NEURAL_ENGINE,
        ),
    )
}
