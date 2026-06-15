package com.shifthackz.aisdv1.domain.feature.bonsai

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

/**
 * Defines runtime support rules for Bonsai model catalog entries.
 *
 * @author Dmitriy Moroz
 */
object BonsaiModelSupport {

    /**
     * Exposes Bonsai model ids that are temporarily disabled in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val unsupportedModelIds = emptySet<String>()

    /**
     * Executes the `isSupported` step in the SDAI domain layer.
     *
     * @param model model value consumed by the operation.
     * @return Result produced by `isSupported`.
     * @author Dmitriy Moroz
     */
    fun isSupported(model: LocalAiModel): Boolean =
        model.type != LocalAiModel.Type.Bonsai || model.id !in unsupportedModelIds
}
