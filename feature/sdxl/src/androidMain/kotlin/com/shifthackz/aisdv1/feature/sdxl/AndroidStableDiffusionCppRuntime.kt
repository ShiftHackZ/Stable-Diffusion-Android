package com.shifthackz.aisdv1.feature.sdxl

import android.graphics.Bitmap
import android.util.Base64
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppProgress
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppRequest
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppResult
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppRuntime
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

internal class AndroidStableDiffusionCppRuntime : StableDiffusionCppRuntime {

    private val mutex = Mutex()

    override suspend fun generate(
        request: StableDiffusionCppRequest,
        onProgress: (StableDiffusionCppProgress) -> Unit,
    ): StableDiffusionCppResult = mutex.withLock {
        withContext(Dispatchers.Default) {
            StableDiffusionCppNativeBridge.ensureLoaded()
            val job = currentCoroutineContext().job
            val cancellationHandle = job.invokeOnCompletion { cause ->
                if (cause is CancellationException) {
                    StableDiffusionCppNativeBridge.interrupt()
                }
            }

            try {
                val shape = IntArray(3)
                val pixels = StableDiffusionCppNativeBridge.generate(
                    modelPath = request.modelPath,
                    prompt = request.prompt,
                    negativePrompt = request.negativePrompt,
                    width = request.width,
                    height = request.height,
                    samplingSteps = request.samplingSteps,
                    cfgScale = request.cfgScale,
                    seed = request.seed,
                    batchCount = request.batchCount,
                    backend = request.backend.ordinal,
                    sampler = request.sampler.ordinal,
                    shape = shape,
                    callback = object : StableDiffusionCppNativeBridge.ProgressCallback {
                        override fun onProgress(current: Int, total: Int) {
                            onProgress(StableDiffusionCppProgress(current, total))
                        }
                    },
                )
                StableDiffusionCppResult(
                    base64Image = pixels
                        .toBitmap(
                            width = shape[0],
                            height = shape[1],
                            channels = shape[2],
                        )
                        .toPngBase64(),
                )
            } finally {
                cancellationHandle.dispose()
            }
        }
    }

    override suspend fun interrupt() {
        StableDiffusionCppNativeBridge.interrupt()
    }
}

private fun ByteArray.toBitmap(
    width: Int,
    height: Int,
    channels: Int,
): Bitmap {
    require(width > 0 && height > 0) { "Invalid image size: ${width}x$height." }
    require(channels == 1 || channels == 3 || channels == 4) {
        "Unsupported image channel count: $channels."
    }

    val colors = IntArray(width * height)
    val expected = colors.size * channels
    require(size >= expected) {
        "Invalid image payload: expected at least $expected bytes, got $size."
    }

    for (index in colors.indices) {
        val source = index * channels
        val r: Int
        val g: Int
        val b: Int
        val a: Int
        when (channels) {
            1 -> {
                val value = this[source].toInt() and 0xFF
                r = value
                g = value
                b = value
                a = 0xFF
            }

            3 -> {
                r = this[source].toInt() and 0xFF
                g = this[source + 1].toInt() and 0xFF
                b = this[source + 2].toInt() and 0xFF
                a = 0xFF
            }

            else -> {
                r = this[source].toInt() and 0xFF
                g = this[source + 1].toInt() and 0xFF
                b = this[source + 2].toInt() and 0xFF
                a = this[source + 3].toInt() and 0xFF
            }
        }
        colors[index] = (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    return Bitmap.createBitmap(colors, width, height, Bitmap.Config.ARGB_8888)
}

private fun Bitmap.toPngBase64(): String =
    ByteArrayOutputStream().use { stream ->
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
