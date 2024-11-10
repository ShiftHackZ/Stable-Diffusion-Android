package com.shifthackz.aisdv1.domain.usecase.gallery

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class ToggleImageVisibilityUseCaseImplTest {

    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = ToggleImageVisibilityUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned value, expected valid boolean value`() {
        whenever(stubRepository.toggleVisibility(any()))
            .thenReturn(Single.just(true))

        useCase(5598L)
            .test()
            .await()
            .assertValue(true)
            .assertComplete()
    }

    @Test
    fun `given repository thrown exception, expected error value`() {
        val stubException = Throwable("Error communicating with MediaStore.")

        whenever(stubRepository.toggleVisibility(any()))
            .thenReturn(Single.error(stubException))

        useCase(5598L)
            .test()
            .await()
            .assertError(stubException)
            .assertNoValues()
            .assertNotComplete()
    }
}
