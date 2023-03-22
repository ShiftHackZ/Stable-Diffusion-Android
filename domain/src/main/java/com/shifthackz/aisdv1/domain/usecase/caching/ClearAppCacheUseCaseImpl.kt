package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable

internal class ClearAppCacheUseCaseImpl(
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val repository: GenerationResultRepository,
) : ClearAppCacheUseCase {

    override fun invoke() = Completable.concatArray(
        repository.deleteAll(),
        Completable.fromAction { FileLoggingTree.clearLog(fileProviderDescriptor) },
    )
}
