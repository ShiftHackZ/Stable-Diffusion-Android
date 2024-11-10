package com.shifthackz.aisdv1.domain.usecase.gallery

import io.reactivex.rxjava3.core.Single

interface ToggleImageVisibilityUseCase {
    operator fun invoke(id: Long): Single<Boolean>
}
