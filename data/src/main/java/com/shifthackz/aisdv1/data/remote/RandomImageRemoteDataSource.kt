package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnRestApi

internal class RandomImageRemoteDataSource(
    private val api: ImageCdnRestApi,
) : RandomImageDataSource.Remote {

    override fun fetch() = api.fetchRandomImage()
}
