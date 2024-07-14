package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

class SelectStableDiffusionModelUseCaseImplTest {

    private val stubException = Throwable("Unknown error occurred.")
    private val stubServerConfigurationRepository = mock<ServerConfigurationRepository>()
    private val stubPreferenceManager = mock<PreferenceManager>()

    private val useCase = SelectStableDiffusionModelUseCaseImpl(
        serverConfigurationRepository = stubServerConfigurationRepository,
        preferenceManager = stubPreferenceManager,
    )

    @Test
    fun `expected get, update, fetch completed, expected complete without errors`() {
        whenever(stubServerConfigurationRepository.getConfiguration())
            .thenReturn(Single.just(mockServerConfiguration))

        whenever(stubServerConfigurationRepository.updateConfiguration(any()))
            .thenReturn(Completable.complete())

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        useCase("model")
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `expected get failed, update, fetch completed, expected complete with error`() {
        whenever(stubServerConfigurationRepository.getConfiguration())
            .thenReturn(Single.error(stubException))

        whenever(stubServerConfigurationRepository.updateConfiguration(any()))
            .thenReturn(Completable.complete())

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        useCase("model")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }


    @Test
    fun `expected update failed, get, fetch completed, expected complete with error`() {
        whenever(stubServerConfigurationRepository.getConfiguration())
            .thenReturn(Single.just(mockServerConfiguration))

        whenever(stubServerConfigurationRepository.updateConfiguration(any()))
            .thenReturn(Completable.error(stubException))

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.complete())

        useCase("model")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `expected get, update completed, fetch failed, expected complete with error`() {
        whenever(stubServerConfigurationRepository.getConfiguration())
            .thenReturn(Single.just(mockServerConfiguration))

        whenever(stubServerConfigurationRepository.updateConfiguration(any()))
            .thenReturn(Completable.complete())

        whenever(stubServerConfigurationRepository.fetchConfiguration())
            .thenReturn(Completable.error(stubException))

        useCase("model")
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
