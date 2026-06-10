package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.mocks.mockEmbeddings
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchAndGetEmbeddingsUseCaseImplTest {

    private val stubRepository = mockk<EmbeddingsRepository>()

    private val useCase = FetchAndGetEmbeddingsUseCaseImpl(stubRepository)

    @Test
    fun `given repository provided embeddings list, expected valid list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetEmbeddings()
        } returns mockEmbeddings

        val actual = useCase()

        Assert.assertEquals(mockEmbeddings, actual)
    }

    @Test
    fun `given repository provided empty embeddings list, expected empty list value`() = runTest {
        coEvery {
            stubRepository.fetchAndGetEmbeddings()
        } returns emptyList()

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Unknown error occurred.")

        coEvery {
            stubRepository.fetchAndGetEmbeddings()
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }
}
