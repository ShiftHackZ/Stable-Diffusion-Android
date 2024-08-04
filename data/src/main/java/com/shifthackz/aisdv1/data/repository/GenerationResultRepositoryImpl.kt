package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.data.core.CoreMediaStoreRepository
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Completable
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

    override fun getAll(): Single<List<AiGenerationResult>> = localDataSource.queryAll()

    override fun getPage(limit: Int, offset: Int): Single<List<AiGenerationResult>> = localDataSource.queryPage(limit, offset)

    override fun getMediaStoreInfo(): Single<MediaStoreInfo> = getInfo()

    override fun getById(id: Long): Single<AiGenerationResult> = localDataSource.queryById(id)

    override fun insert(result: AiGenerationResult): Single<Long> = localDataSource
        .insert(result)
        .flatMap { id -> exportToMediaStore(result).andThen(Single.just(id)) }

    override fun deleteById(id: Long): Completable = localDataSource.deleteById(id)

    override fun deleteAll(): Completable = localDataSource.deleteAll()
}
