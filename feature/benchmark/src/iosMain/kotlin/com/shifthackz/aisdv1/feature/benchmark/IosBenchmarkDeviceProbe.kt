package com.shifthackz.aisdv1.feature.benchmark

import platform.Foundation.NSProcessInfo
import platform.Metal.MTLCreateSystemDefaultDevice
import platform.UIKit.UIDevice
import platform.posix.getenv
import platform.posix.uname
import platform.posix.utsname
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString

/**
 * Collects iOS hardware metadata without constructing Core ML pipelines.
 *
 * Metal/Core ML support is inferred from public system capabilities only, so a
 * missing model package or unstable diffusion runtime cannot affect benchmark
 * collection.
 *
 * @author Dmitriy Moroz
 */
internal class IosBenchmarkDeviceProbe : BenchmarkDeviceProbe {

    override suspend fun capture(): BenchmarkDeviceInfo = runCatching {
        val processInfo = NSProcessInfo.processInfo
        val metalDevice = runCatching { MTLCreateSystemDefaultDevice() }.getOrNull()
        val simulatorDeviceName = environment(SIMULATOR_DEVICE_NAME)
        val hardwareIdentifier = if (simulatorDeviceName == null) hardwareIdentifier() else null
        val physicalMemoryMb = processInfo.physicalMemory.toLong().bytesToMb()
        BenchmarkDeviceInfo(
            platform = BenchmarkPlatform.IOS,
            manufacturer = "Apple",
            model = simulatorDeviceName
                ?: hardwareIdentifier?.appleMarketingName()
                ?: UIDevice.currentDevice.model.orUnknown(),
            osVersion = "${UIDevice.currentDevice.systemName.orUnknown()} " +
                UIDevice.currentDevice.systemVersion.orUnknown(),
            cpuName = "Apple Silicon",
            cpuCores = processInfo.processorCount.toInt().coerceAtLeast(1),
            gpuName = metalDevice?.name ?: "Apple GPU",
            totalRamMb = physicalMemoryMb.simulatorAdjustedRam(simulatorDeviceName != null),
            availableRamMb = physicalMemoryMb.simulatorAdjustedRam(simulatorDeviceName != null),
            totalVramMb = null,
            availableVramMb = null,
            accelerators = accelerators(
                metalAvailable = metalDevice != null,
                simulator = simulatorDeviceName != null,
            ),
        )
    }.getOrElse { fallback() }

    private fun accelerators(
        metalAvailable: Boolean,
        simulator: Boolean,
    ): List<BenchmarkAccelerator> = buildList {
        if (metalAvailable) add(BenchmarkAccelerator.METAL)
        add(BenchmarkAccelerator.CORE_ML)
        if (!simulator) add(BenchmarkAccelerator.NEURAL_ENGINE)
    }

    private fun Long.simulatorAdjustedRam(simulator: Boolean): Long =
        if (simulator && this > SIMULATOR_RAM_CAP_MB) SIMULATOR_RAM_CAP_MB else this

    @OptIn(ExperimentalForeignApi::class)
    private fun environment(name: String): String? =
        getenv(name)?.toKString()?.takeIf(String::isNotBlank)

    @OptIn(ExperimentalForeignApi::class)
    private fun hardwareIdentifier(): String? = memScoped {
        val info = alloc<utsname>()
        if (uname(info.ptr) != 0) return@memScoped null
        info.machine.toKString().takeIf(String::isNotBlank)
    }

    private fun fallback(): BenchmarkDeviceInfo = BenchmarkDeviceInfo(
        platform = BenchmarkPlatform.IOS,
        manufacturer = "Apple",
        model = hardwareIdentifier()?.appleMarketingName() ?: UIDevice.currentDevice.model.orUnknown(),
        osVersion = UIDevice.currentDevice.systemVersion.orUnknown(),
        cpuName = "Apple Silicon",
        cpuCores = 1,
        gpuName = "Apple GPU",
        totalRamMb = 1024L,
        availableRamMb = 1024L,
        totalVramMb = null,
        availableVramMb = null,
        accelerators = listOf(BenchmarkAccelerator.CORE_ML),
    )

    private fun Long.bytesToMb(): Long = (this / BYTES_IN_MB).coerceAtLeast(0L)

    private fun String?.orUnknown(): String = this?.takeIf(String::isNotBlank) ?: UNKNOWN

    private fun String.appleMarketingName(): String? = when (this) {
        "iPhone18,1" -> "iPhone 17 Pro"
        "iPhone18,2" -> "iPhone 17 Pro Max"
        "iPhone18,3" -> "iPhone 17"
        "iPhone18,4" -> "iPhone Air"
        "iPhone18,5" -> "iPhone 17e"
        "iPhone17,1" -> "iPhone 16 Pro"
        "iPhone17,2" -> "iPhone 16 Pro Max"
        "iPhone17,3" -> "iPhone 16"
        "iPhone17,4" -> "iPhone 16 Plus"
        "iPhone17,5" -> "iPhone 16e"
        "iPhone16,1" -> "iPhone 15 Pro"
        "iPhone16,2" -> "iPhone 15 Pro Max"
        "iPhone15,4" -> "iPhone 15"
        "iPhone15,5" -> "iPhone 15 Plus"
        else -> null
    }

    private companion object {
        private const val BYTES_IN_MB = 1024L * 1024L
        private const val SIMULATOR_DEVICE_NAME = "SIMULATOR_DEVICE_NAME"
        private const val SIMULATOR_RAM_CAP_MB = 8_192L
        private const val UNKNOWN = "Unknown"
    }
}
