package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ObserveHordeProcessStatusUseCaseImplTest {

    private val stubException = Throwable("Error communicating with Horde.")
    private val stubHordeStatus = BehaviorSubject.create<HordeProcessStatus>()
    private val stubRepository = mock<HordeGenerationRepository>()

    private val useCase = ObserveHordeProcessStatusUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.observeStatus())
            .thenReturn(stubHordeStatus.toFlowable(BackpressureStrategy.LATEST))
    }

    @Test
    fun `given repository emits two different values, expected two valid values`() {
        val stubObserver = useCase().test()

        stubHordeStatus.onNext(HordeProcessStatus(5598, 1504))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, HordeProcessStatus(5598, 1504))

        stubHordeStatus.onNext(HordeProcessStatus(0, 0))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, HordeProcessStatus(0, 0))
            .assertValueCount(2)
    }

    @Test
    fun `given repository emits two same values, expected one valid value`() {
        val stubObserver = useCase().test()

        stubHordeStatus.onNext(HordeProcessStatus(5598, 1504))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, HordeProcessStatus(5598, 1504))

        stubHordeStatus.onNext(HordeProcessStatus(5598, 1504))

        stubObserver
            .assertNoErrors()
            .assertValueCount(1)
    }

    @Test
    fun `given repository throws exception, expected error value`() {
        whenever(stubRepository.observeStatus())
            .thenReturn(Flowable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
