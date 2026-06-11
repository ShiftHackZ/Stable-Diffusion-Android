package com.shifthackz.aisdv1.domain.feature.coreml

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

/**
 * Defines runtime support rules for Core ML model catalog entries.
 *
 * @author Dmitriy Moroz
 */
object CoreMlModelSupport {

    /**
     * Exposes Core ML model ids that are temporarily disabled in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val unsupportedModelIds = setOf(
        "apple-coreml-sdxl-base-mixed-bit-palettized-original",
        "apple-coreml-sdxl-base-original",
    )

    /**
     * Executes the `isSupported` step in the SDAI domain layer.
     *
     * @param model model value consumed by the operation.
     * @return Result produced by `isSupported`.
     * @author Dmitriy Moroz
     */
    fun isSupported(model: LocalAiModel): Boolean =
        model.type != LocalAiModel.Type.CoreMl || model.id !in unsupportedModelIds
}
