package com.shifthackz.aisdv1.domain.usecase.gallery

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.MediaStoreInfo
import com.shifthackz.aisdv1.domain.mocks.mockMediaStoreInfo
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class GetMediaStoreInfoUseCaseImplTest {

    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = GetMediaStoreInfoUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided media store info, expected valid media store info`() {
        whenever(stubRepository.getMediaStoreInfo())
            .thenReturn(Single.just(mockMediaStoreInfo))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(mockMediaStoreInfo)
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository provided empty media store info, expected default media store info`() {
        whenever(stubRepository.getMediaStoreInfo())
            .thenReturn(Single.just(MediaStoreInfo()))

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(MediaStoreInfo())
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected error value`() {
        val stubException = Throwable("Error communicating with MediaStore.")

        whenever(stubRepository.getMediaStoreInfo())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
