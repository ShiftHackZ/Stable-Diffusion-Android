package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DefaultConnectToA1111UseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()
    private val stubTestConnectivityUseCase = mockk<TestConnectivityUseCase>()
    private val stubDataPreLoaderUseCase = mockk<DataPreLoaderUseCase>()

    private val useCase = DefaultConnectToA1111UseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
        testConnectivityUseCase = stubTestConnectivityUseCase,
        dataPreLoaderUseCase = stubDataPreLoaderUseCase,
    )

    @Test
    fun `given connection process successful, expected success result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } returns mockConfiguration

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } returns Unit

        coEvery {
            stubTestConnectivityUseCase(any())
        } returns Unit

        coEvery {
            stubDataPreLoaderUseCase()
        } returns Unit

        val actual = useCase("5598", false, AuthorizationCredentials.None)

        assertEquals(Result.success(Unit), actual)
    }

    @Test
    fun `given connection process failed, expected error result value`() = runTest {
        coEvery {
            stubGetConfigurationUseCase()
        } throws stubThrowable

        coEvery {
            stubSetServerConfigurationUseCase(any())
        } throws stubThrowable

        coEvery {
            stubTestConnectivityUseCase(any())
        } throws stubThrowable

        coEvery {
            stubDataPreLoaderUseCase()
        } throws stubThrowable

        val actual = useCase("5598", false, AuthorizationCredentials.None)

        assertTrue(actual.isFailure)
        assertEquals(stubThrowable.message, actual.exceptionOrNull()?.message)
    }
}
