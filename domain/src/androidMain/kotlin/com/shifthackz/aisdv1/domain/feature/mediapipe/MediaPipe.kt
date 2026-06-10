package com.shifthackz.aisdv1.domain.feature.mediapipe

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `MediaPipe` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface MediaPipe {
    /**
     * Executes the `process` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @return Result produced by `process`.
     * @author Dmitriy Moroz
     */
    suspend fun process(payload: TextToImagePayload): Bitmap
}
