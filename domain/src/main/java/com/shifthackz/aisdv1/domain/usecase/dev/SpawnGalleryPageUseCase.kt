package com.shifthackz.aisdv1.domain.usecase.dev

import io.reactivex.rxjava3.core.Completable

interface SpawnGalleryPageUseCase {
    operator fun invoke(amount: Int): Completable
}
