package com.shifthackz.aisdv1.domain.usecase.debug

import io.reactivex.rxjava3.core.Completable

interface DebugInsertBadBase64UseCase {
    operator fun invoke(): Completable
}
