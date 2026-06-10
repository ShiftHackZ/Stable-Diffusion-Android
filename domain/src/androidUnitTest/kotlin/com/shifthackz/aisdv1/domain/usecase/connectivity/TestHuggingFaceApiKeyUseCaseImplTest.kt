package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultTestHuggingFaceApiKeyUseCaseImplTest {

    private val stubException = RuntimeException("Can not connect to Hugging Face AI.")
    private val stubConfigurationStore = ConfigurationStoreStub()
    private val stubRemoteDataSource = mockk<HuggingFaceGenerationDataSource.Remote>()

    private val useCase = DefaultTestHuggingFaceApiKeyUseCaseImpl(
        configurationStore = stubConfigurationStore,
        remoteDataSource = stubRemoteDataSource,
    )

    @Test
    fun `given hugging face api key passed validation, expected true`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.huggingFaceApiKey)
        } returns true

        assertEquals(true, useCase())
    }

    @Test
    fun `given hugging face api key not passed validation, expected false`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.huggingFaceApiKey)
        } returns false

        assertEquals(false, useCase())
    }

    @Test
    fun `given validator thrown exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.huggingFaceApiKey)
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
