package com.shifthackz.aisdv1.feature.diffusion.ai.vae

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtSession.SessionOptions
import ai.onnxruntime.providers.NNAPIFlags
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.diffusion.utils.PathManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.util.EnumSet
import kotlin.math.roundToInt

class VaeDecoder(private val context: Context, private val deviceId: Int) : KoinComponent {

    private val ortEnvironmentProvider: OrtEnvironmentProvider by inject()
    private val model = "vae_decoder/model.ort"

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
        output: Array<Array<Array<FloatArray>>>,
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
        options.addConfigEntry("session.load_model_format", "ORT")
        if (deviceId == LocalDiffusionFlag.NN_API.value) options.addNnapi(EnumSet.of(NNAPIFlags.CPU_DISABLED))
        val file = File(PathManager.getCustomPath(context) + "/" + model)
        session = ortEnvironmentProvider.get().createSession(
            if (file.exists()) file.absolutePath else PathManager.getModelPath(
                context
            ) + "/" + model, options
        )
    }

    private fun clamp(value: Double, min: Double = 0.0, max: Double = 1.0): Double = when {
        value < min -> min
        value > max -> max
        else -> value
    }
}
