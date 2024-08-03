package com.shifthackz.aisdv1.data.remote

import android.graphics.Bitmap
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnRestApi
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class RandomImageRemoteDataSourceTest {

    private val stubException = Throwable("Internal server error.")
    private val stubBitmap = mockk<Bitmap>()
    private val stubApi = mockk<ImageCdnRestApi>()

    private val remoteDataSource = RandomImageRemoteDataSource(stubApi)

    @Test
    fun `given attempt to fetch bitmap with random image, api request succeed, expected valid bitmap value`() {
        every {
            stubApi.fetchRandomImage()
        } returns Single.just(stubBitmap)

        remoteDataSource
            .fetch()
            .test()
            .assertNoErrors()
            .assertValue(stubBitmap)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch bitmap with random image, api request fails, expected error value`() {
        every {
            stubApi.fetchRandomImage()
        } returns Single.error(stubException)

        remoteDataSource
            .fetch()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
