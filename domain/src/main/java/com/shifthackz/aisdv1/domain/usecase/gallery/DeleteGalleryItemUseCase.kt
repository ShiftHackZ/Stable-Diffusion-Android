package com.shifthackz.aisdv1.domain.usecase.gallery

import io.reactivex.rxjava3.core.Completable

interface DeleteGalleryItemUseCase {
    operator fun invoke(id: Long): Completable
}
