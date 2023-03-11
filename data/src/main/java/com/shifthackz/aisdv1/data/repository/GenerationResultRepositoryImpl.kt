package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

class GenerationResultRepositoryImpl(
    private val localDataSource: GenerationResultDataSource.Local,
) : GenerationResultRepository {

    override fun getAll() = localDataSource.queryAll()

    override fun getPage(limit: Int, offset: Int) = localDataSource.queryPage(limit, offset)

    override fun getById(id: Long) = localDataSource.queryById(id)

    override fun insert(result: AiGenerationResult) = localDataSource.insert(result)

    override fun deleteById(id: Long) = localDataSource.deleteById(id)

    override fun deleteAll() = localDataSource.deleteAll()
}
