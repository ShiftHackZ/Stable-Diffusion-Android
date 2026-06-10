package com.shifthackz.aisdv1.domain.usecase.sdhypernet

import com.shifthackz.aisdv1.domain.mocks.mockStableDiffusionHyperNetworks
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchAndGetHyperNetworksUseCaseImplTest {

    private val stubRepository = mockk<StableDiffusionHyperNetworksRepository>()

    private val useCase = FetchAndGetHyperNetworksUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided list of hypernetworks, expected valid list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetHyperNetworks()
        } returns mockStableDiffusionHyperNetworks

        val actual = useCase()

        Assert.assertEquals(mockStableDiffusionHyperNetworks, actual)
    }

    @Test
    fun `given repository provided empty list of hypernetworks, expected empty list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetHyperNetworks()
        } returns emptyList()

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Unknown error occurred.")

        coEvery {
            stubRepository.fetchAndGetHyperNetworks()
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
