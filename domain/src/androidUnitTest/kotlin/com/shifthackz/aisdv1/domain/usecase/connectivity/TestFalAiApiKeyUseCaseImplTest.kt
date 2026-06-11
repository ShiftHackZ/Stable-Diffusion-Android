package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.FalAiGenerationDataSource
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultTestFalAiApiKeyUseCaseImplTest {

    private val stubException = RuntimeException("Can not connect to Fal.ai.")
    private val stubConfigurationStore = ConfigurationStoreStub()
    private val stubRemoteDataSource = mockk<FalAiGenerationDataSource.Remote>()

    private val useCase = DefaultTestFalAiApiKeyUseCaseImpl(
        configurationStore = stubConfigurationStore,
        remoteDataSource = stubRemoteDataSource,
    )

    @Test
    fun `given fal ai api key passed validation, expected true`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.falAiApiKey)
        } returns true

        assertEquals(true, useCase())
    }

    @Test
    fun `given fal ai api key not passed validation, expected false`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.falAiApiKey)
        } returns false

        assertEquals(false, useCase())
    }

    @Test
    fun `given validator thrown exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.falAiApiKey)
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
