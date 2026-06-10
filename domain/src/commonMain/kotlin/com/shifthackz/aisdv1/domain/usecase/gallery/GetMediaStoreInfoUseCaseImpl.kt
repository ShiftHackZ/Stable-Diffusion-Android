package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway

/**
 * Implements `GetMediaStoreInfoUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class GetMediaStoreInfoUseCaseImpl(
    /**
     * Exposes the `mediaStoreGateway` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val mediaStoreGateway: MediaStoreGateway,
) : GetMediaStoreInfoUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = mediaStoreGateway.getInfo()
}
