package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mocks.mockAiGenerationResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TemporaryGenerationResultRepositoryImplTest {

    private val repository = TemporaryGenerationResultRepositoryImpl()

    @Test
    fun `given cache is empty, then get, expected error value`() = runTest {
        val actual = runCatching { repository.get() }

        Assert.assertTrue(actual.exceptionOrNull() is IllegalStateException)
        Assert.assertEquals("No last cached result.", actual.exceptionOrNull()?.message)
    }

    @Test
    fun `given cache contains value, then get, expected valid cached value`() = runTest {
        val putResult = runCatching { repository.put(mockAiGenerationResult) }

        Assert.assertTrue(putResult.isSuccess)
        Assert.assertEquals(mockAiGenerationResult, repository.get())
    }
}
