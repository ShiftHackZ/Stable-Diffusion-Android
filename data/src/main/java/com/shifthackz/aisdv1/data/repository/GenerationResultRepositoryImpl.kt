package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class GenerationResultRepositoryImpl(
    private val localDataSource: GenerationResultDataSource.Local,
) : GenerationResultRepository {

    override fun getAll() = localDataSource.queryAll()

    override fun getPage(limit: Int, offset: Int) = localDataSource.queryPage(limit, offset)
}
