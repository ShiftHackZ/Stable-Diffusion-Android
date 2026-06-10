package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultTestOpenAiApiKeyUseCaseImplTest {

    private val stubException = RuntimeException("Can not connect to OpenAI.")
    private val stubConfigurationStore = ConfigurationStoreStub()
    private val stubRemoteDataSource = mockk<OpenAiGenerationDataSource.Remote>()

    private val useCase = DefaultTestOpenAiApiKeyUseCaseImpl(
        configurationStore = stubConfigurationStore,
        remoteDataSource = stubRemoteDataSource,
    )

    @Test
    fun `given openai api key passed validation, expected true`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.openAiApiKey)
        } returns true

        assertEquals(true, useCase())
    }

    @Test
    fun `given openai api key not passed validation, expected false`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.openAiApiKey)
        } returns false

        assertEquals(false, useCase())
    }

    @Test
    fun `given validator thrown exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.openAiApiKey)
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
