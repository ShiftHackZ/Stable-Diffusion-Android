package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.mocks.ConfigurationStoreStub
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultTestHordeApiKeyUseCaseImplTest {

    private val stubException = RuntimeException("Can not connect to Horde AI.")
    private val stubConfigurationStore = ConfigurationStoreStub()
    private val stubRemoteDataSource = mockk<HordeGenerationDataSource.Remote>()

    private val useCase = DefaultTestHordeApiKeyUseCaseImpl(
        configurationStore = stubConfigurationStore,
        remoteDataSource = stubRemoteDataSource,
    )

    @Test
    fun `given horde api key passed validation, expected true`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.hordeApiKey)
        } returns true

        assertEquals(true, useCase())
    }

    @Test
    fun `given horde api key not passed validation, expected false`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.hordeApiKey)
        } returns false

        assertEquals(false, useCase())
    }

    @Test
    fun `given validator thrown exception, expected error value`() = runTest {
        coEvery {
            stubRemoteDataSource.validateApiKey(stubConfigurationStore.hordeApiKey)
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
