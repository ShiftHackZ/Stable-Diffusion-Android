package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel

/**
 * Implements `GetLocalModelUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetLocalModelUseCaseImpl(
    /**
     * Exposes the `localDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: DownloadableModelDataSource.Local,
) : GetLocalModelUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(id: String): LocalAiModel = localDataSource.getById(id)
}
