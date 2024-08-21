package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface BackupRepository {
    fun create(tokens: List<Pair<BackupEntryToken, Boolean>>): Single<ByteArray>
    fun restore(bytes: ByteArray): Completable
}
