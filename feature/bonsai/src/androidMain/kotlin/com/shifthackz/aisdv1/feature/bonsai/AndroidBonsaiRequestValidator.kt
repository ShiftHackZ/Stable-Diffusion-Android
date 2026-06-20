package com.shifthackz.aisdv1.feature.bonsai

import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import java.io.File

/**
 * Validates Android Bonsai generation requests before entering the native runtime.
 *
 * @author Dmitriy Moroz
 */
internal object AndroidBonsaiRequestValidator {

    fun validate(
        payload: TextToImagePayload,
        modelPath: String,
    ) {
        if (payload.prompt.isBlank()) {
            throw IllegalStateException("Prompt is required.")
        }

        if (payload.width <= 0 ||
            payload.height <= 0 ||
            payload.width % 32 != 0 ||
            payload.height % 32 != 0
        ) {
            throw IllegalStateException("Bonsai image size must be positive and divisible by 32.")
        }

        if (!File(modelPath).exists()) {
            throw IllegalStateException("Bonsai model resources were not found at $modelPath.")
        }
    }
}
