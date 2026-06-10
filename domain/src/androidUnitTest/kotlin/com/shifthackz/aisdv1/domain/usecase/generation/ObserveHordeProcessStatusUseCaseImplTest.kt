package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveHordeProcessStatusUseCaseImplTest {

    private val stubException = Throwable("Error communicating with Horde.")
    private val stubHordeStatus = MutableSharedFlow<HordeProcessStatus>()
    private val stubRepository = mock<HordeGenerationRepository>()

    private val useCase = ObserveHordeProcessStatusUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.observeStatus())
            .thenReturn(stubHordeStatus)
    }

    @Test
    fun `given repository emits two different values, expected two valid values`() = runTest {
        val values = mutableListOf<HordeProcessStatus>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubHordeStatus.emit(HordeProcessStatus(5598, 1504))
        stubHordeStatus.emit(HordeProcessStatus(0, 0))
        job.join()

        Assert.assertEquals(
            listOf(HordeProcessStatus(5598, 1504), HordeProcessStatus(0, 0)),
            values,
        )
    }

    @Test
    fun `given repository emits two same values, expected one valid value`() = runTest {
        val values = mutableListOf<HordeProcessStatus>()
        val job = launch {
            useCase()
                .take(2)
                .toList(values)
        }
        runCurrent()

        stubHordeStatus.emit(HordeProcessStatus(5598, 1504))
        stubHordeStatus.emit(HordeProcessStatus(5598, 1504))
        advanceUntilIdle()
        job.cancelAndJoin()

        Assert.assertEquals(listOf(HordeProcessStatus(5598, 1504)), values)
    }

    @Test
    fun `given repository throws exception, expected error value`() = runTest {
        whenever(stubRepository.observeStatus())
            .thenReturn(flow { throw stubException })

        val actual = runCatching { useCase().collect() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
