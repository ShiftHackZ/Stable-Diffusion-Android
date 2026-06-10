package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
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
class ObserveLocalDiffusionProcessStatusUseCaseImplTest {

    private val stubException = Throwable("Error loading Local Diffusion.")
    private val stubLocalStatus = MutableSharedFlow<LocalDiffusionStatus>()
    private val stubRepository = mock<LocalDiffusionGenerationRepository>()

    private val useCase = ObserveLocalDiffusionProcessStatusUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.observeStatus())
            .thenReturn(stubLocalStatus)
    }

    @Test
    fun `given repository processes three steps, expected three valid status values`() = runTest {
        val values = mutableListOf<LocalDiffusionStatus>()
        val job = launch {
            useCase()
                .take(3)
                .toList(values)
        }
        runCurrent()

        stubLocalStatus.emit(LocalDiffusionStatus(1, 3))
        stubLocalStatus.emit(LocalDiffusionStatus(2, 3))
        stubLocalStatus.emit(LocalDiffusionStatus(3, 3))
        job.join()

        assertEquals(
            listOf(
                LocalDiffusionStatus(1, 3),
                LocalDiffusionStatus(2, 3),
                LocalDiffusionStatus(3, 3),
            ),
            values,
        )
    }

    @Test
    fun `given repository processes two steps, emits same step twice, expected two valid status values`() = runTest {
        val values = mutableListOf<LocalDiffusionStatus>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubLocalStatus.emit(LocalDiffusionStatus(1, 2))
        stubLocalStatus.emit(LocalDiffusionStatus(1, 2))
        advanceUntilIdle()

        assertEquals(listOf(LocalDiffusionStatus(1, 2)), values)

        stubLocalStatus.emit(LocalDiffusionStatus(2, 2))
        job.join()

        assertEquals(
            listOf(
                LocalDiffusionStatus(1, 2),
                LocalDiffusionStatus(2, 2),
            ),
            values,
        )
    }

    @Test
    fun `given repository throws exception, expected error value`() = runTest {
        whenever(stubRepository.observeStatus())
            .thenReturn(flow { throw stubException })

        val actual = runCatching { useCase().collect() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
