package com.shifthackz.aisdv1.domain.usecase.debug

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class DebugInsertBadBase64UseCaseImplTest {

    private val stubRepository = mock<GenerationResultRepository>()

    private val useCase = DebugInsertBadBase64UseCaseImpl(stubRepository)

    @Test
    fun `given inserted value with bad BASE64 into DB, expected complete value`() {
        whenever(stubRepository.insert(any()))
            .thenReturn(Single.just(5598L))

        useCase()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given failed to insert value with bad BASE64 into DB, expected error value`() {
        val stubException = Throwable("DB error.")

        whenever(stubRepository.insert(any()))
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
