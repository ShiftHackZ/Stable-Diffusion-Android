package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.mocks.mockHuggingFaceModels
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class FetchHuggingFaceModelsUseCaseImplTest {

    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubRepository = mockk<HuggingFaceModelsRepository>()

    private val useCase = FetchHuggingFaceModelsUseCaseImpl(
        preferenceManager = stubPreferenceManager,
        repository = stubRepository,
    )

    @Test
    fun `given repository provided models list, expected valid list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.HUGGING_FACE

        coEvery {
            stubRepository.fetchAndGetHuggingFaceModels()
        } returns mockHuggingFaceModels

        val actual = useCase()

        Assert.assertEquals(mockHuggingFaceModels, actual)
    }

    @Test
    fun `given repository provided empty models list, expected empty list value`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.HUGGING_FACE

        coEvery {
            stubRepository.fetchAndGetHuggingFaceModels()
        } returns emptyList()

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
    }

    @Test
    fun `given repository thrown exception, expected error value`() = runTest {
        val stubException = Throwable("Unknown error occurred.")

        every {
            stubPreferenceManager.source
        } returns ServerSource.HUGGING_FACE

        coEvery {
            stubRepository.fetchAndGetHuggingFaceModels()
        } throws stubException

        val actual = runCatching { useCase() }

        Assert.assertEquals(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given inactive source, expected empty list and no remote fetch`() = runTest {
        every {
            stubPreferenceManager.source
        } returns ServerSource.AUTOMATIC1111

        val actual = useCase()

        Assert.assertEquals(emptyList<Any>(), actual)
        coVerify(exactly = 0) {
            stubRepository.fetchAndGetHuggingFaceModels()
        }
    }
}
