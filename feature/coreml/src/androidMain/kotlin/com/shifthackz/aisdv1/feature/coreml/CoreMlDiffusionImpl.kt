package com.shifthackz.aisdv1.feature.coreml

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.coreml.CoreMlDiffusion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implements Android noop behavior for the iOS-only Core ML feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class CoreMlDiffusionImpl : CoreMlDiffusion {

    /**
     * Executes the `process` step in the SDAI Core ML feature layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @author Dmitriy Moroz
     */
    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String {
        throw IllegalStateException("Silicon Diffusion Core ML is available on iOS only.")
    }

    /**
     * Executes the `process` step in the SDAI Core ML feature layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @author Dmitriy Moroz
     */
    override suspend fun process(
        payload: ImageToImagePayload,
        modelPath: String,
    ): String {
        throw IllegalStateException("Silicon Diffusion Core ML is available on iOS only.")
    }

    /**
     * Performs the SDAI side effect handled by `interrupt`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interrupt() = Unit

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<LocalDiffusionStatus> = flowOf(LocalDiffusionStatus(0, 0))
}
