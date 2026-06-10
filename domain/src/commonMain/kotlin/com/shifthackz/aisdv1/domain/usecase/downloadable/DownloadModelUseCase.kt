package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.DownloadState
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `DownloadModelUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DownloadModelUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @param url remote URL used by the operation.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(id: String, url: String): Flow<DownloadState>
}
