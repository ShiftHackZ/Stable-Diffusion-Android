package com.shifthackz.aisdv1.domain.demo

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Provides the `NoOpTextToImageDemo` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpTextToImageDemo : TextToImageDemo {
    /**
     * Loads SDAI data through `getDemoBase64`.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun getDemoBase64(payload: TextToImagePayload) =
        error("Demo mode is not available on this platform.")
}

/**
 * Provides the `NoOpImageToImageDemo` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpImageToImageDemo : ImageToImageDemo {
    /**
     * Loads SDAI data through `getDemoBase64`.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun getDemoBase64(payload: ImageToImagePayload) =
        error("Demo mode is not available on this platform.")
}
