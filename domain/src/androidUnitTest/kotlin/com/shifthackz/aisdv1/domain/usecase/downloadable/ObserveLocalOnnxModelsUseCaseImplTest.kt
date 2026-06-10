package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveLocalOnnxModelsUseCaseImplTest {

    private val stubLocalModels = MutableSharedFlow<List<LocalAiModel>>()
    private val stubRepository = mockk<DownloadableModelRepository>()

    private val useCase = ObserveLocalOnnxModelsUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        every {
            stubRepository.observeAllOnnx()
        } returns stubLocalModels
    }

    @Test
    fun `given repository has empty model list, then list inserted, expected receive empty list value, then valid list value`() = runTest {
        val values = mutableListOf<List<LocalAiModel>>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubLocalModels.emit(emptyList())
        stubLocalModels.emit(mockLocalAiModels)
        job.join()

        assertEquals(listOf(emptyList<LocalAiModel>(), mockLocalAiModels), values)
    }

    @Test
    fun `given repository has model list, then clear, expected receive valid list value, then empty list value`() = runTest {
        val values = mutableListOf<List<LocalAiModel>>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubLocalModels.emit(mockLocalAiModels)
        stubLocalModels.emit(emptyList())
        job.join()

        assertEquals(listOf(mockLocalAiModels, emptyList<LocalAiModel>()), values)
    }

    @Test
    fun `given repository has model list, then list changes, expected receive valid list value, then changed list value`() = runTest {
        val changedLocalAiModels = listOf(LocalAiModel.CustomOnnx)
        val values = mutableListOf<List<LocalAiModel>>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubLocalModels.emit(mockLocalAiModels)
        stubLocalModels.emit(changedLocalAiModels)
        job.join()

        assertEquals(listOf(mockLocalAiModels, changedLocalAiModels), values)
    }

    @Test
    fun `given repository observer has model list, emits twice, expected receive valid list value once`() = runTest {
        val values = mutableListOf<List<LocalAiModel>>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubLocalModels.emit(mockLocalAiModels)
        stubLocalModels.emit(mockLocalAiModels)
        advanceUntilIdle()
        job.cancelAndJoin()

        assertEquals(listOf(mockLocalAiModels), values)
    }

    @Test
    fun `given observer terminates with unexpected error, expected receive error value`() = runTest {
        val stubException = Throwable("Unexpected Flow termination.")

        every {
            stubRepository.observeAllOnnx()
        } returns flow { throw stubException }

        val actual = runCatching { useCase().collect() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
