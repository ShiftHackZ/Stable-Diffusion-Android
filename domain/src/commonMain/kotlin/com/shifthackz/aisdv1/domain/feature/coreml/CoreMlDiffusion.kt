package com.shifthackz.aisdv1.domain.feature.coreml

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `CoreMlDiffusion` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface CoreMlDiffusion {
    /**
     * Executes the `process` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @return Base64-encoded generated image.
     * @author Dmitriy Moroz
     */
    suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String

    /**
     * Executes the `process` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @return Base64-encoded generated image.
     * @author Dmitriy Moroz
     */
    suspend fun process(
        payload: ImageToImagePayload,
        modelPath: String,
    ): String

    /**
     * Performs the SDAI side effect handled by `interrupt`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun interrupt()

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    fun observeStatus(): Flow<LocalDiffusionStatus>
}
