package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreMediaStoreRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single

internal class GenerationResultRepositoryImpl(
    preferenceManager: PreferenceManager,
    mediaStoreGateway: MediaStoreGateway,
    base64ToBitmapConverter: Base64ToBitmapConverter,
    private val localDataSource: GenerationResultDataSource.Local,
) : CoreMediaStoreRepository(
    preferenceManager,
    mediaStoreGateway,
    base64ToBitmapConverter,
), GenerationResultRepository {

    override fun getAll() = localDataSource.queryAll()

    override fun getPage(limit: Int, offset: Int) = localDataSource.queryPage(limit, offset)

    override fun getMediaStoreInfo() = getInfo()

    override fun getById(id: Long) = localDataSource.queryById(id)

    override fun getByIds(idList: List<Long>) = localDataSource.queryByIdList(idList)

    override fun insert(result: AiGenerationResult) = localDataSource
        .insert(result)
        .flatMap { id -> exportToMediaStore(result).andThen(Single.just(id)) }

    override fun deleteById(id: Long) = localDataSource.deleteById(id)

    override fun deleteByIdList(idList: List<Long>) = localDataSource.deleteByIdList(idList)

    override fun deleteAll() = localDataSource.deleteAll()
}
