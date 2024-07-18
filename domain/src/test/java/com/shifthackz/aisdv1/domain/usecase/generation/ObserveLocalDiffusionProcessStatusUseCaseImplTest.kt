package com.shifthackz.aisdv1.domain.usecase.generation

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ObserveLocalDiffusionProcessStatusUseCaseImplTest {

    private val stubException = Throwable("Error loading Local Diffusion.")
    private val stubLocalStatus = BehaviorSubject.create<LocalDiffusion.Status>()
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

        stubLocalStatus.onNext(LocalDiffusion.Status(1, 3))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, LocalDiffusion.Status(1, 3))

        stubLocalStatus.onNext(LocalDiffusion.Status(2, 3))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, LocalDiffusion.Status(2, 3))

        stubLocalStatus.onNext(LocalDiffusion.Status(3, 3))

        stubObserver
            .assertNoErrors()
            .assertValueAt(2, LocalDiffusion.Status(3, 3))
            .assertValueCount(3)
    }

    @Test
    fun `given repository processes two steps, emits same step twice, expected two valid status values`() {
        val stubObserver = useCase().test()

        stubLocalStatus.onNext(LocalDiffusion.Status(1, 2))

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, LocalDiffusion.Status(1, 2))

        stubLocalStatus.onNext(LocalDiffusion.Status(1, 2))

        stubObserver
            .assertNoErrors()
            .assertValueCount(1)

        stubLocalStatus.onNext(LocalDiffusion.Status(2, 2))

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, LocalDiffusion.Status(2, 2))
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
