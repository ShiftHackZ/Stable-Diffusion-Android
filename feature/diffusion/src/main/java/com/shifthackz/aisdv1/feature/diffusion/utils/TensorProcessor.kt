package com.shifthackz.aisdv1.feature.diffusion.utils

import ai.onnxruntime.OnnxTensor.*
import android.util.Pair
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionTensor
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.FloatBuffer

internal object TensorProcessor : KoinComponent {

    private val ortEnvironmentProvider: OrtEnvironmentProvider by inject()

    fun duplicate(data: FloatArray, dimensions: LongArray?): LocalDiffusionTensor<*> {
        val floats = FloatArray(data.size * 2)
        System.arraycopy(data, 0, floats, 0, data.size)
        System.arraycopy(data, 0, floats, data.size, data.size)
        return LocalDiffusionTensor(
            tensor = createTensor(
                ortEnvironmentProvider.get(),
                FloatBuffer.wrap(floats),
                dimensions
            ),
            buffer = null,
            shape = dimensions,
        )
    }

    fun multipleTensorsByFloat(
        data: FloatArray,
        value: Float,
        dimensions: LongArray?,
    ): LocalDiffusionTensor<*> {
        for (i in data.indices) {
            data[i] = data[i] * value
        }
        return LocalDiffusionTensor(
            tensor = createTensor(
                ortEnvironmentProvider.get(),
                FloatBuffer.wrap(data),
                dimensions
            ),
            buffer = null,
            shape = dimensions,
        )
    }

    fun splitTensor(
        tensorToSplit: Array<Array<Array<FloatArray>>>,
        dimensions: LongArray,
    ): Pair<Array<Array<Array<FloatArray>>>, Array<Array<Array<FloatArray>>>> {
        val firstTensor = Array(dimensions[0].toInt()) {
            Array(dimensions[1].toInt()) {
                Array(dimensions[2].toInt()) {
                    FloatArray(
                        dimensions[3].toInt()
                    )
                }
            }
        }
        val secondTensor = Array(dimensions[0].toInt()) {
            Array(dimensions[1].toInt()) {
                Array(dimensions[2].toInt()) {
                    FloatArray(
                        dimensions[3].toInt()
                    )
                }
            }
        }
        for (i in 0..0) {
            for (j in 0..3) {
                for (k in 0 until dimensions[2]) {
                    for (l in 0 until dimensions[3]) {
                        firstTensor[i][j][k.toInt()][l.toInt()] =
                            tensorToSplit[i][j][k.toInt()][l.toInt()]
                        secondTensor[i][j][k.toInt()][l.toInt()] =
                            tensorToSplit[i + 1][j][k.toInt()][l.toInt()]
                    }
                }
            }
        }
        return Pair(firstTensor, secondTensor)
    }
}
