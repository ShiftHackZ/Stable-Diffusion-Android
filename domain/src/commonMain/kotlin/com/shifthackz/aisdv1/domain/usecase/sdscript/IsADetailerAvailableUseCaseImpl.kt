package com.shifthackz.aisdv1.domain.usecase.sdscript

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionScriptsRepository

/**
 * Implements `IsADetailerAvailableUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class IsADetailerAvailableUseCaseImpl(
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: StableDiffusionScriptsRepository,
) : IsADetailerAvailableUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): Boolean {
        if (preferenceManager.source != ServerSource.AUTOMATIC1111) return false
        return repository.fetchScripts().contains(ADETAILER_SCRIPT_NAME)
    }

    private companion object {
        const val ADETAILER_SCRIPT_NAME = "ADetailer"
    }
}
