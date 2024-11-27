package com.shifthackz.aisdv1.domain.usecase.backup

import com.shifthackz.aisdv1.domain.repository.BackupRepository

internal class RestoreBackupUseCaseImpl(
    private val backupRepository: BackupRepository,
) : RestoreBackupUseCase {

    override fun invoke(data: ByteArray) = backupRepository.restore(data)
}
