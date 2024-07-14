package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.mocks.mockServerConfiguration
import com.shifthackz.aisdv1.domain.mocks.mockStableDiffusionModels
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import io.reactivex.rxjava3.core.Single
import org.junit.Assert
import org.junit.Test

class GetStableDiffusionModelsUseCaseImplTest {

    private val stubServerConfigurationRepository = mock<ServerConfigurationRepository>()
    private val stubSdModelsRepository = mock<StableDiffusionModelsRepository>()

    private val useCase = GetStableDiffusionModelsUseCaseImpl(
        serverConfigurationRepository = stubServerConfigurationRepository,
        sdModelsRepository = stubSdModelsRepository,
    )

    @Test
    fun `given repository returns list with value present in configuration, expected list with selected value`() {
        whenever(stubServerConfigurationRepository.fetchAndGetConfiguration())
            .thenReturn(Single.just(mockServerConfiguration))

        whenever(stubSdModelsRepository.fetchAndGetModels())
            .thenReturn(Single.just(mockStableDiffusionModels))

        val expectedValue = mockStableDiffusionModels.map {
            it to (it.title == mockServerConfiguration.sdModelCheckpoint)
        }

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(expectedValue)
            .also {
                Assert.assertEquals(
                    true,
                    expectedValue.any { (_, selected) -> selected },
                )
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given repository returns list with no value present in configuration, expected list without selected value`() {
        val stubServerConfiguration = ServerConfiguration("nonsense")

        whenever(stubServerConfigurationRepository.fetchAndGetConfiguration())
            .thenReturn(Single.just(stubServerConfiguration))

        whenever(stubSdModelsRepository.fetchAndGetModels())
            .thenReturn(Single.just(mockStableDiffusionModels))

        val expectedValue = mockStableDiffusionModels.map {
            it to (it.title == stubServerConfiguration.sdModelCheckpoint)
        }

        useCase()
            .test()
            .assertNoErrors()
            .assertValue(expectedValue)
            .also {
                Assert.assertEquals(
                    true,
                    !expectedValue.any { (_, selected) -> selected },
                )
            }
            .await()
            .assertComplete()
    }

    @Test
    fun `given exception while fetching configuration, expected error value`() {
        val stubException = Throwable("Network error.")

        whenever(stubServerConfigurationRepository.fetchAndGetConfiguration())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given exception while fetching models, expected error value`() {
        val stubException = Throwable("Network error.")

        whenever(stubServerConfigurationRepository.fetchAndGetConfiguration())
            .thenReturn(Single.just(mockServerConfiguration))

        whenever(stubSdModelsRepository.fetchAndGetModels())
            .thenReturn(Single.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
