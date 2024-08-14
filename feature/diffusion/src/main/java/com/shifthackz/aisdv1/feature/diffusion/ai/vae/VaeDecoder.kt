package com.shifthackz.aisdv1.feature.diffusion.ai.vae

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtSession.SessionOptions
import ai.onnxruntime.providers.NNAPIFlags
import android.graphics.Bitmap
import android.graphics.Color
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.ORT
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.ORT_KEY_MODEL_FORMAT
import com.shifthackz.aisdv1.feature.diffusion.entity.Array3D
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.diffusion.environment.LocalModelIdProvider
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.extensions.modelPathPrefix
import java.util.EnumSet
import kotlin.math.roundToInt

internal class VaeDecoder(
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val localModelIdProvider: LocalModelIdProvider,
    private val preferenceManager: PreferenceManager,
    private val deviceId: Int,
) {

    private var session: OrtSession? = null

    fun decode(input: Map<String?, OnnxTensor?>?): Any {
        initialize()
        val result = session!!.run(input)
        val value = result[0].value
        result.close()
        close()
        return value
    }

    fun convertToImage(
        output: Array3D<FloatArray>,
        width: Int,
        height: Int,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val r = (clamp(output[0][0][y][x] / 2 + 0.5) * 255f).roundToInt()
                val g = (clamp(output[0][1][y][x] / 2 + 0.5) * 255f).roundToInt()
                val b = (clamp(output[0][2][y][x] / 2 + 0.5) * 255f).roundToInt()
                val color = Color.rgb(r, g, b)
                bitmap.setPixel(x, y, color)
            }
        }
        return bitmap
    }

    fun close() {
        session?.close()
        session = null
    }

    private fun initialize() {
        if (session != null) return
        val options = SessionOptions()
        options.addConfigEntry(ORT_KEY_MODEL_FORMAT, ORT)
        if (deviceId == LocalDiffusionFlag.NN_API.value) {
            options.addNnapi(EnumSet.of(NNAPIFlags.CPU_DISABLED))
        }
        session = ortEnvironmentProvider.get().createSession(
            "${modelPathPrefix(preferenceManager, fileProviderDescriptor, localModelIdProvider)}/${LocalDiffusionContract.VAE_MODEL}",
            options
        )
    }

    private fun clamp(value: Double, min: Double = 0.0, max: Double = 1.0): Double = when {
        value < min -> min
        value > max -> max
        else -> value
    }
}
