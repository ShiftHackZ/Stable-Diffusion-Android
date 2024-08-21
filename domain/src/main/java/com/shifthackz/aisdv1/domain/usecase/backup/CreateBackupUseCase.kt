package com.shifthackz.aisdv1.domain.usecase.backup

import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import io.reactivex.rxjava3.core.Single

interface CreateBackupUseCase {
    operator fun invoke(tokens: List<Pair<BackupEntryToken, Boolean>>): Single<ByteArray>
}
