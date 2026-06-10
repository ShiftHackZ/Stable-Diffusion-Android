package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnApi

/**
 * Coordinates `RandomImageRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class RandomImageRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: ImageCdnApi,
) : RandomImageDataSource.Remote {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetch(): ByteArray = api.fetchRandomImageBytes()
}
