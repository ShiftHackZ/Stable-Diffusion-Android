package com.shifthackz.aisdv1.feature.bonsai

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.bonsai.BonsaiDiffusion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Implements Android noop behavior for the iOS-only Bonsai feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class BonsaiDiffusionImpl : BonsaiDiffusion {

    /**
     * Executes the `process` step in the SDAI Bonsai feature layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @author Dmitriy Moroz
     */
    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String {
        throw IllegalStateException("Bonsai Image generation is available on iOS only.")
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
