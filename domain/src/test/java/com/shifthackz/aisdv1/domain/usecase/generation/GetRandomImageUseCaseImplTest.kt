package com.shifthackz.aisdv1.domain.usecase.generation

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetRandomImageUseCaseImplTest {

    private val stubException = Throwable("Can not generate random image.")
    private val stubBitmap = mock<Bitmap>()
    private val stubRepository = mock<RandomImageRepository>()

    private val useCase = GetRandomImageUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided bitmap with random image, expected valid bitmap value`() {
        whenever(stubRepository.fetchAndGet())
            .thenReturn(Single.just(stubBitmap))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(stubBitmap)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected error value`() {
        whenever(stubRepository.fetchAndGet())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
