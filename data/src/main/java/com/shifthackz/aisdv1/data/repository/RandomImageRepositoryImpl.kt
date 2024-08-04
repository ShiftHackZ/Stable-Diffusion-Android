package com.shifthackz.aisdv1.data.repository

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository
import io.reactivex.rxjava3.core.Single

internal class RandomImageRepositoryImpl(
    private val remoteDataSource: RandomImageDataSource.Remote,
) : RandomImageRepository {

    override fun fetchAndGet(): Single<Bitmap> = remoteDataSource.fetch()
}
