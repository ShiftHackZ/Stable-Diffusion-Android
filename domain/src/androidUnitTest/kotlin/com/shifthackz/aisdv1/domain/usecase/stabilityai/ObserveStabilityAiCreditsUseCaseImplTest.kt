package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveStabilityAiCreditsUseCaseImplTest {

    private val stubCredits = MutableSharedFlow<Float>(replay = 2)
    private val stubStabilityAiCreditsRepository = mockk<StabilityAiCreditsRepository>()

    private val useCase = ObserveStabilityAiCreditsUseCaseImpl(
        repository = stubStabilityAiCreditsRepository,
    )

    @Test
    fun `given successfully got credits, expected valid credits value`() = runTest {
        every {
            stubStabilityAiCreditsRepository.fetchAndObserve()
        } returns stubCredits

        val actual = mutableListOf<Float>()
        val job = launch {
            useCase()
                .take(1)
                .toList(actual)
        }

        stubCredits.emit(5598f)
        job.join()

        assertEquals(listOf(5598f), actual)
    }

    @Test
    fun `given successfully got credits, then credits changed, expected credits value changed`() = runTest {
        every {
            stubStabilityAiCreditsRepository.fetchAndObserve()
        } returns stubCredits

        val actual = mutableListOf<Float>()
        val job = launch {
            useCase()
                .take(2)
                .toList(actual)
        }

        stubCredits.emit(5598f)
        stubCredits.emit(2211f)
        job.join()

        assertEquals(listOf(5598f, 2211f), actual)
    }

    @Test
    fun `given exception from credits repository, expected zero credits value`() = runTest {
        every {
            stubStabilityAiCreditsRepository.fetchAndObserve()
        } returns flow<Float> { throw Throwable("Wrong server source selected.") }

        val actual = useCase().toList()

        assertEquals(listOf(0f), actual)
    }
}
