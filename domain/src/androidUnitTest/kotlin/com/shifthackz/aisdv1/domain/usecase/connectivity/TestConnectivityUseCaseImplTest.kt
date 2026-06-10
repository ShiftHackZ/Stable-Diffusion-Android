package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultTestConnectivityUseCaseImplTest {

    companion object {
        private const val STUB_URL = "https://5598.is.my.favourite.com"
    }

    private val stubException = RuntimeException("Can not establish connection to server.")
    private val stubRemoteDataSource = mockk<StableDiffusionGenerationDataSource.Remote>()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val useCase = DefaultTestConnectivityUseCaseImpl(
        remoteDataSource = stubRemoteDataSource,
        authorizationStore = stubAuthorizationStore,
    )

    @Test
    fun `given connection to server can be established, expected complete value`() = runTest {
        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        coEvery {
            stubRemoteDataSource.checkAvailability(STUB_URL, AuthorizationCredentials.None)
        } returns Unit

        useCase(STUB_URL)
    }

    @Test
    fun `given connection to server can not be established, expected error value`() = runTest {
        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        coEvery {
            stubRemoteDataSource.checkAvailability(STUB_URL, AuthorizationCredentials.None)
        } throws stubException

        val actual = runCatching { useCase(STUB_URL) }.exceptionOrNull()

        assertTrue(actual === stubException)
    }
}
