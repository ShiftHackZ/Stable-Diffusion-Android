package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import org.junit.Test

class TemporaryGenerationResultRepositoryImplTest {

    private val repository = TemporaryGenerationResultRepositoryImpl()

    @Test
    fun `given cache is empty, then get, expected error value`() {
        repository
            .get()
            .test()
            .assertError { t ->
                t is IllegalStateException && t.message == "No last cached result."
            }
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given cache contains value, then get, expected valid cached value`() {
        repository
            .put(mockAiGenerationResult)
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()

        repository
            .get()
            .test()
            .assertNoErrors()
            .assertValue(mockAiGenerationResult)
            .await()
            .assertComplete()
    }
}
