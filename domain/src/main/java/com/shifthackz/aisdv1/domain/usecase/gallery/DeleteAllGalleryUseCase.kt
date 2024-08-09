package com.shifthackz.aisdv1.domain.usecase.gallery

import io.reactivex.rxjava3.core.Completable

interface DeleteAllGalleryUseCase {
    operator fun invoke(): Completable
}
