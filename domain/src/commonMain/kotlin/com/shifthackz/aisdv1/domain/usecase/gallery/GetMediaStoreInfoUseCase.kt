package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo

interface GetMediaStoreInfoUseCase {
    suspend operator fun invoke(): MediaStoreInfo
}
