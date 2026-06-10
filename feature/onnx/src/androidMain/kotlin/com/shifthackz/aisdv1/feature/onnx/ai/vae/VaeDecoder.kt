package com.shifthackz.aisdv1.feature.onnx.ai.vae

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtSession.SessionOptions
import ai.onnxruntime.providers.NNAPIFlags
import android.graphics.Bitmap
import android.graphics.Color
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.ORT
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.ORT_KEY_MODEL_FORMAT
import com.shifthackz.aisdv1.feature.onnx.entity.Array3D
import com.shifthackz.aisdv1.feature.onnx.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.onnx.environment.LocalModelIdProvider
import com.shifthackz.aisdv1.feature.onnx.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.onnx.extensions.modelPathPrefix
import java.util.EnumSet
import kotlin.math.roundToInt

/**
 * Coordinates `VaeDecoder` behavior in the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class VaeDecoder(
    /**
     * Exposes the `ortEnvironmentProvider` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
    /**
     * Exposes the `fileProviderDescriptor` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val fileProviderDescriptor: FileProviderDescriptor,
    /**
     * Exposes the `localModelIdProvider` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val localModelIdProvider: LocalModelIdProvider,
    /**
     * Exposes the `preferenceManager` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `deviceId` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val deviceId: Int,
) {

    /**
     * Exposes the `session` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private var session: OrtSession? = null

    /**
     * Executes the `decode` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `decode`.
     * @author Dmitriy Moroz
     */
    fun decode(input: Map<String?, OnnxTensor?>?): Any {
        initialize()
        val result = session!!.run(input)
        val value = result[0].value
        result.close()
        close()
        return value
    }

    /**
     * Executes the `convertToImage` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param output output value consumed by the API.
     * @param width width value consumed by the API.
     * @param height height value consumed by the API.
     * @return Result produced by `convertToImage`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `close` step in the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    fun close() {
        session?.close()
        session = null
    }

    /**
     * Executes the `initialize` step in the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
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

    /**
     * Executes the `clamp` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param value value value consumed by the API.
     * @param min min value consumed by the API.
     * @param max max value consumed by the API.
     * @return Result produced by `clamp`.
     * @author Dmitriy Moroz
     */
    private fun clamp(value: Double, min: Double = 0.0, max: Double = 1.0): Double = when {
        value < min -> min
        value > max -> max
        else -> value
    }
}
