package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GetLocalOnnxModelsUseCaseImplTest {

    private val stubRepository = mockk<DownloadableModelRepository>()

    private val useCase = GetLocalOnnxModelsUseCaseImpl(stubRepository)

    @Test
    fun `given repository returned models list, expected valid models list value`() = runTest {
        coEvery {
            stubRepository.getAllOnnx()
        } returns mockLocalAiModels

        val actual = useCase()

        assertEquals(mockLocalAiModels, actual)
    }

    @Test
    fun `given repository returned empty models list, expected empty models list value`() = runTest {
        coEvery {
            stubRepository.getAllOnnx()
        } returns emptyList()

        val actual = useCase()

        assertTrue(actual.isEmpty())
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Unable to collect local models.")

        coEvery {
            stubRepository.getAllOnnx()
        } throws stubException

        val actual = runCatching { useCase() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
