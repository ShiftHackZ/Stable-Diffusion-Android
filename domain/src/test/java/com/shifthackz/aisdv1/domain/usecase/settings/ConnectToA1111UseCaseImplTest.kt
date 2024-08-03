package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class ConnectToA1111UseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()
    private val stubTestConnectivityUseCase = mockk<TestConnectivityUseCase>()
    private val stubDataPreLoaderUseCase = mockk<DataPreLoaderUseCase>()

    private val useCase = ConnectToA1111UseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
        testConnectivityUseCase = stubTestConnectivityUseCase,
        dataPreLoaderUseCase = stubDataPreLoaderUseCase,
    )

    @Test
    fun `given connection process successful, expected success result value`() {
        every {
            stubGetConfigurationUseCase()
        } returns Single.just(mockConfiguration)

        every {
            stubSetServerConfigurationUseCase(any())
        } returns Completable.complete()

        every {
            stubTestConnectivityUseCase(any())
        } returns Completable.complete()

        every {
            stubDataPreLoaderUseCase()
        } returns Completable.complete()

        useCase("5598", false, AuthorizationCredentials.None)
            .test()
            .assertNoErrors()
            .await()
            .assertValue(Result.success(Unit))
            .assertComplete()
    }

    @Test
    fun `given connection process failed, expected error result value`() {
        every {
            stubGetConfigurationUseCase()
        } returns Single.error(stubThrowable)

        every {
            stubSetServerConfigurationUseCase(any())
        } returns Completable.error(stubThrowable)

        every {
            stubTestConnectivityUseCase(any())
        } returns Completable.error(stubThrowable)

        every {
            stubDataPreLoaderUseCase()
        } returns Completable.error(stubThrowable)

        useCase("5598", false, AuthorizationCredentials.None)
            .test()
            .assertNoErrors()
            .await()
            .assertValue(Result.failure(stubThrowable))
            .assertComplete()
    }
}
