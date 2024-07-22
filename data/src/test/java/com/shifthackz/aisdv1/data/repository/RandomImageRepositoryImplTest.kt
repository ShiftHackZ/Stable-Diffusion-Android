package com.shifthackz.aisdv1.data.repository

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class RandomImageRepositoryImplTest {

    private val stubBitmap = mockk<Bitmap>()
    private val stubException = Throwable("Something went wrong.")
    private val stubRemoteDataSource = mockk<RandomImageDataSource.Remote>()

    private val repository = RandomImageRepositoryImpl(stubRemoteDataSource)

    @Test
    fun `given attempt to fetch and get bitmap, remote returns image, expected valid bitmap value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.just(stubBitmap)

        repository
            .fetchAndGet()
            .test()
            .assertNoErrors()
            .assertValue(stubBitmap)
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to fetch and get bitmap, remote throws exception, expected valid bitmap value`() {
        every {
            stubRemoteDataSource.fetch()
        } returns Single.error(stubException)

        repository
            .fetchAndGet()
            .test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
