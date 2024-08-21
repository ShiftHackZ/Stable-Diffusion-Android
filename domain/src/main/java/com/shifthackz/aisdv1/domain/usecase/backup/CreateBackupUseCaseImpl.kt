package com.shifthackz.aisdv1.domain.usecase.backup

import com.shifthackz.aisdv1.domain.entity.BackupEntryToken
import com.shifthackz.aisdv1.domain.repository.BackupRepository

internal class CreateBackupUseCaseImpl(
    private val backupRepository: BackupRepository,
) : CreateBackupUseCase {

    override fun invoke(tokens: List<Pair<BackupEntryToken, Boolean>>) = backupRepository
        .create(tokens)
}
