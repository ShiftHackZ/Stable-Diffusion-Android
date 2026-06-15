package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.repository.BonsaiGenerationRepository
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
class ObserveBonsaiProcessStatusUseCaseImplTest {

    private val stubException = Throwable("Error loading Bonsai status.")
    private val stubLocalStatus = MutableSharedFlow<LocalDiffusionStatus>()
    private val stubRepository = mockk<BonsaiGenerationRepository>()

    private val useCase = ObserveBonsaiProcessStatusUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        every {
            stubRepository.observeStatus()
        } returns stubLocalStatus
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
    fun `given repository emits same step twice, expected one status value`() = runTest {
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
        job.cancelAndJoin()

        assertEquals(listOf(LocalDiffusionStatus(1, 2)), values)
    }

    @Test
    fun `given repository throws exception, expected error value`() = runTest {
        every {
            stubRepository.observeStatus()
        } returns flow { throw stubException }

        val actual = runCatching { useCase().collect() }.exceptionOrNull()

        assertSame(stubException, actual)
    }
}
