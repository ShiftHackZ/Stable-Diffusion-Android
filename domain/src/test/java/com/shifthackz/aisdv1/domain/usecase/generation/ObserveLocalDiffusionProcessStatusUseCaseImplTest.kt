package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ObserveLocalDiffusionProcessStatusUseCaseImplTest {

    private val stubException = Throwable("Error loading Local Diffusion.")
    private val stubLocalStatus = BehaviorSubject.create<LocalDiffusionStatus>()
    private val stubRepository = mock<LocalDiffusionGenerationRepository>()

    private val useCase = ObserveLocalDiffusionProcessStatusUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.observeStatus())
            .thenReturn(stubLocalStatus)
    }

    @Test
    fun `given repository processes three steps, expected three valid status values`() {
        val stubObserver = useCase().test()

        stubLocalStatus.onNext(LocalDiffusionStatus(1, 3))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, LocalDiffusionStatus(1, 3))

        stubLocalStatus.onNext(LocalDiffusionStatus(2, 3))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, LocalDiffusionStatus(2, 3))

        stubLocalStatus.onNext(LocalDiffusionStatus(3, 3))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, LocalDiffusionStatus(3, 3))
            .assertValueCount(3)
    }

    @Test
    fun `given repository processes two steps, emits same step twice, expected two valid status values`() {
        val stubObserver = useCase().test()

        stubLocalStatus.onNext(LocalDiffusionStatus(1, 2))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, LocalDiffusionStatus(1, 2))

        stubLocalStatus.onNext(LocalDiffusionStatus(1, 2))

        stubObserver
            .assertNoErrors()
            .assertValueCount(1)

        stubLocalStatus.onNext(LocalDiffusionStatus(2, 2))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, LocalDiffusionStatus(2, 2))
            .assertValueCount(2)
    }

    @Test
    fun `given repository throws exception, expected error value`() {
        whenever(stubRepository.observeStatus())
            .thenReturn(Observable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertValueCount(0)
            .assertNotComplete()
    }
}
