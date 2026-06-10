package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

/**
 * Defines the `GetMediaStoreInfoUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetMediaStoreInfoUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): MediaStoreInfo
}
