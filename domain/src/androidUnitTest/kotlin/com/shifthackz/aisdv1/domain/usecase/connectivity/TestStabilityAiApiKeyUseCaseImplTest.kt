package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultTestStabilityAiApiKeyUseCaseImplTest {

    private val stubException = RuntimeException("Can not connect to Stability AI.")
    private val stubConfigurationStore = ConfigurationStoreStub()
    private val stubRemoteDataSource = mockk<StabilityAiGenerationDataSource.Remote>()

    private val useCase = DefaultTestStabilityAiApiKeyUseCaseImpl(
        configurationStore = stubConfigurationStore,
        remoteDataSource = stubRemoteDataSource,
    )

    @Test
    fun `given Stability AI api key passed validation, expected true`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.stabilityAiApiKey)
        } returns true

        assertEquals(true, useCase())
    }

    @Test
    fun `given Stability AI api key not passed validation, expected false`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.stabilityAiApiKey)
        } returns false

        assertEquals(false, useCase())
    }

    @Test
    fun `given validator thrown exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.stabilityAiApiKey)
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
