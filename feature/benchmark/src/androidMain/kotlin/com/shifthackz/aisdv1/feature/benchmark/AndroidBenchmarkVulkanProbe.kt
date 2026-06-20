package com.shifthackz.aisdv1.feature.benchmark

import android.util.Log

/**
 * Runs a benchmark-scoped native Vulkan probe for custom Bonsai compute kernels.
 *
 * This does not initialize Bonsai models or generation runtime.
 *
 * @author Dmitriy Moroz
 */
internal object AndroidBenchmarkVulkanProbe {

    private val loadResult = runCatching {
        System.loadLibrary(LIBRARY_NAME)
    }

    fun capture(): AndroidBenchmarkVulkanProbeResult {
        val result = loadResult.fold(
            onSuccess = {
                runCatching { probeVulkan().toProbeResult() }
                    .getOrElse { error ->
                        AndroidBenchmarkVulkanProbeResult(
                            apiDetected = false,
                            usable = false,
                            summary = "usable=false;apiDetected=false;reason=probe_failed:${error.message}",
                        )
                    }
            },
            onFailure = { error ->
                AndroidBenchmarkVulkanProbeResult(
                    apiDetected = false,
                    usable = false,
                    summary = "usable=false;apiDetected=false;reason=native_unavailable:${error.message}",
                )
            },
        )
        Log.i(LOG_TAG, "bonsai_vulkan_probe ${result.summary}")
        return result
    }

    private external fun probeVulkan(): String
}

internal data class AndroidBenchmarkVulkanProbeResult(
    val apiDetected: Boolean,
    val usable: Boolean,
    val summary: String,
)

private fun String.toProbeResult(): AndroidBenchmarkVulkanProbeResult =
    AndroidBenchmarkVulkanProbeResult(
        apiDetected = containsKeyValue("apiDetected", "true"),
        usable = containsKeyValue("usable", "true"),
        summary = this,
    )

private fun String.containsKeyValue(
    key: String,
    value: String,
): Boolean = split(';')
    .any { item -> item.substringBefore('=') == key && item.substringAfter('=') == value }

private const val LIBRARY_NAME = "sdai_benchmark"
private const val LOG_TAG = "SDAI-Benchmark"
