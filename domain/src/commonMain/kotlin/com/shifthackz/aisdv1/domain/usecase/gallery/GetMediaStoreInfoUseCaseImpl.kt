package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway

class GetMediaStoreInfoUseCaseImpl(
    private val mediaStoreGateway: MediaStoreGateway,
) : GetMediaStoreInfoUseCase {

    override suspend fun invoke() = mediaStoreGateway.getInfo()
}
