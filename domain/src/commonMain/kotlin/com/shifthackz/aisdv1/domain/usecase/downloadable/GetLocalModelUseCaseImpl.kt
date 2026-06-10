package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.datasource.DownloadableModelDataSource
import com.shifthackz.aisdv1.domain.entity.LocalAiModel

internal class GetLocalModelUseCaseImpl(
    private val localDataSource: DownloadableModelDataSource.Local,
) : GetLocalModelUseCase {

    override suspend fun invoke(id: String): LocalAiModel = localDataSource.getById(id)
}
