package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnApi

internal class RandomImageRemoteDataSource(
    private val api: ImageCdnApi,
) : RandomImageDataSource.Remote {

    override suspend fun fetch(): ByteArray = api.fetchRandomImageBytes()
}
