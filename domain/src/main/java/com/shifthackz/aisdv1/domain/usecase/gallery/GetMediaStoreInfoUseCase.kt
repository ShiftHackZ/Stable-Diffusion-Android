package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import io.reactivex.rxjava3.core.Single

interface GetMediaStoreInfoUseCase {
    operator fun invoke(): Single<MediaStoreInfo>
}
