package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class ConnectToLocalDiffusionUseCaseImplTest {

    private val stubThrowable = Throwable("Something went wrong.")
    private val stubGetConfigurationUseCase = mockk<GetConfigurationUseCase>()
    private val stubSetServerConfigurationUseCase = mockk<SetServerConfigurationUseCase>()

    private val useCase = ConnectToLocalDiffusionUseCaseImpl(
        getConfigurationUseCase = stubGetConfigurationUseCase,
        setServerConfigurationUseCase = stubSetServerConfigurationUseCase,
    )

    @Test
    fun `given connection process successful, expected success result value`() {
        every {
            stubGetConfigurationUseCase()
        } returns Single.just(mockConfiguration)

        every {
            stubSetServerConfigurationUseCase(any())
        } returns Completable.complete()

        useCase("5598")
            .test()
            .assertNoErrors()
            .assertValue(Result.success(Unit))
            .await()
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

        useCase("5598")
            .test()
            .assertNoErrors()
            .assertValue(Result.failure(stubThrowable))
            .await()
            .assertComplete()
    }
}
