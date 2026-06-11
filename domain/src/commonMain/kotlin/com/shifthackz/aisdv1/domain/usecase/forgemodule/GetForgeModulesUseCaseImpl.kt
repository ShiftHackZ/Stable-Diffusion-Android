package com.shifthackz.aisdv1.domain.usecase.forgemodule

import com.shifthackz.aisdv1.domain.entity.ForgeModule
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ForgeModulesRepository

/**
 * Implements `GetForgeModulesUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetForgeModulesUseCaseImpl(
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
    private val repository: ForgeModulesRepository,
) : GetForgeModulesUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke(): List<ForgeModule> {
        if (preferenceManager.source != ServerSource.AUTOMATIC1111) return emptyList()

        return runCatching { repository.fetchModules() }.getOrDefault(emptyList())
    }
}
