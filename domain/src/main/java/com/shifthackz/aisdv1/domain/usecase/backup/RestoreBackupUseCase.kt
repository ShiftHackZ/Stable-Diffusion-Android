package com.shifthackz.aisdv1.domain.usecase.backup

import io.reactivex.rxjava3.core.Completable

interface RestoreBackupUseCase {
    operator fun invoke(data: ByteArray): Completable
}
