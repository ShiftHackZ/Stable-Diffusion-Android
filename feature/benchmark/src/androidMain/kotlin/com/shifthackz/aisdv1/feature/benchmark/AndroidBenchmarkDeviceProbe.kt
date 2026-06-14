package com.shifthackz.aisdv1.feature.benchmark

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import java.io.File

/**
 * Collects Android hardware metadata without touching AI inference runtimes.
 *
 * All platform calls are best-effort and guarded because vendor builds can hide
 * or partially implement the APIs used for GPU and accelerator discovery.
 *
 * @author Dmitriy Moroz
 */
internal class AndroidBenchmarkDeviceProbe(
    private val context: Context,
) : BenchmarkDeviceProbe {

    override suspend fun capture(): BenchmarkDeviceInfo = runCatching {
        val memoryInfo = activityManager()?.let { manager ->
            ActivityManager.MemoryInfo().also(manager::getMemoryInfo)
        }
        BenchmarkDeviceInfo(
            platform = BenchmarkPlatform.ANDROID,
            manufacturer = Build.MANUFACTURER.orUnknown(),
            model = Build.MODEL.orUnknown(),
            osVersion = "Android ${Build.VERSION.RELEASE.orUnknown()} (${Build.VERSION.SDK_INT})",
            cpuName = cpuName(),
            cpuCores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1),
            gpuName = gpuName(),
            totalRamMb = memoryInfo?.totalMem?.bytesToMb() ?: maxMemoryMb(),
            availableRamMb = memoryInfo?.availMem?.bytesToMb() ?: maxMemoryMb(),
            totalVramMb = null,
            availableVramMb = null,
            accelerators = accelerators(),
        )
    }.getOrElse { fallback() }

    private fun activityManager(): ActivityManager? =
        context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager

    private fun cpuName(): String = runCatching {
        File("/proc/cpuinfo")
            .takeIf(File::canRead)
            ?.useLines { lines ->
                lines.firstNotNullOfOrNull { line ->
                    val normalized = line.trim()
                    when {
                        normalized.startsWith("Hardware", ignoreCase = true) ->
                            normalized.substringAfter(':').trim()
                        normalized.startsWith("model name", ignoreCase = true) ->
                            normalized.substringAfter(':').trim()
                        normalized.startsWith("Processor", ignoreCase = true) ->
                            normalized.substringAfter(':').trim()
                        else -> null
                    }?.takeIf(String::isNotBlank)
                }
            }
    }.getOrNull() ?: socModel().orUnknown()

    private fun gpuName(): String {
        val candidates = listOf(
            socManufacturer(),
            socModel(),
            Build.HARDWARE,
        ).filter { it.isNotBlank() }
        return candidates.joinToString(" / ").ifBlank { "Android GPU" }
    }

    private fun accelerators(): List<BenchmarkAccelerator> = buildList {
        if (hasSystemFeature("android.hardware.vulkan.level") ||
            hasSystemFeature("android.hardware.vulkan.version")
        ) {
            add(BenchmarkAccelerator.VULKAN)
        }
        if (hasOpenClLibrary()) add(BenchmarkAccelerator.OPEN_CL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            add(BenchmarkAccelerator.NNAPI)
        }
    }.distinct()

    private fun hasSystemFeature(feature: String): Boolean = runCatching {
        context.packageManager.hasSystemFeature(feature)
    }.getOrDefault(false)

    private fun hasOpenClLibrary(): Boolean = OPEN_CL_LIBRARY_PATHS.any { path ->
        runCatching { File(path).exists() }.getOrDefault(false)
    }

    private fun fallback(): BenchmarkDeviceInfo = BenchmarkDeviceInfo(
        platform = BenchmarkPlatform.ANDROID,
        manufacturer = Build.MANUFACTURER.orUnknown(),
        model = Build.MODEL.orUnknown(),
        osVersion = "Android ${Build.VERSION.SDK_INT}",
        cpuName = socModel().orUnknown(),
        cpuCores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1),
        gpuName = Build.HARDWARE.orUnknown(),
        totalRamMb = maxMemoryMb(),
        availableRamMb = maxMemoryMb(),
        totalVramMb = null,
        availableVramMb = null,
        accelerators = emptyList(),
    )

    private fun socManufacturer(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Build.SOC_MANUFACTURER else ""

    private fun socModel(): String =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Build.SOC_MODEL else Build.HARDWARE

    private fun maxMemoryMb(): Long =
        Runtime.getRuntime().maxMemory().bytesToMb().coerceAtLeast(512L)

    private fun Long.bytesToMb(): Long = (this / BYTES_IN_MB).coerceAtLeast(0L)

    private fun String?.orUnknown(): String = this?.takeIf(String::isNotBlank) ?: UNKNOWN

    private companion object {
        private const val BYTES_IN_MB = 1024L * 1024L
        private const val UNKNOWN = "Unknown"
        private val OPEN_CL_LIBRARY_PATHS = listOf(
            "/system/vendor/lib/libOpenCL.so",
            "/system/vendor/lib64/libOpenCL.so",
            "/vendor/lib/libOpenCL.so",
            "/vendor/lib64/libOpenCL.so",
            "/system/lib/libOpenCL.so",
            "/system/lib64/libOpenCL.so",
        )
    }
}
