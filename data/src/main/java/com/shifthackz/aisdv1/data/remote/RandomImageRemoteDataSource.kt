package com.shifthackz.aisdv1.data.remote

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnRestApi
import io.reactivex.rxjava3.core.Single

internal class RandomImageRemoteDataSource(
    private val api: ImageCdnRestApi,
) : RandomImageDataSource.Remote {

    override fun fetch(): Single<Bitmap> = api.fetchRandomImage()
}
