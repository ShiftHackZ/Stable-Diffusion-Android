package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.mocks.mockLocalBonsaiModels
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetLocalBonsaiModelsUseCaseImplTest {

    private val stubRepository = mockk<DownloadableModelRepository>()

    private val useCase = GetLocalBonsaiModelsUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned models list, expected valid models list value`() = runTest {
        coEvery {
            stubRepository.getAllBonsai()
        } returns mockLocalBonsaiModels

        val actual = useCase()

        assertEquals(mockLocalBonsaiModels, actual)
    }

    @Test
    fun `given repository returned empty models list, expected empty models list value`() = runTest {
        coEvery {
            stubRepository.getAllBonsai()
        } returns emptyList()

        val actual = useCase()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Unable to collect Bonsai models.")
        coEvery {
            stubRepository.getAllBonsai()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
