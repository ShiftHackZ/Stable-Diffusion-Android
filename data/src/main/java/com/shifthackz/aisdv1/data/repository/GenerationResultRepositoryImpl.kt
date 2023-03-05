package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class GenerationResultRepositoryImpl(
    private val localDataSource: GenerationResultDataSource.Local,
) : GenerationResultRepository {

    override fun getPage(limit: Int, offset: Int) = localDataSource.getPage(limit, offset)
}
