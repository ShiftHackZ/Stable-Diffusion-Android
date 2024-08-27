package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.mocks.mockLocalAiModels
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ObserveLocalOnnxModelsUseCaseImplTest {

    private val stubLocalModels = BehaviorSubject.create<List<LocalAiModel>>()
    private val stubRepository = mock<DownloadableModelRepository>()

    private val useCase = ObserveLocalOnnxModelsUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.observeAllOnnx())
            .thenReturn(stubLocalModels.toFlowable(BackpressureStrategy.LATEST))
    }

    @Test
    fun `given repository has empty model list, then list inserted, expected receive empty list value, then valid list value`() {
        val stubObserver = useCase().test()

        stubLocalModels.onNext(emptyList())

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, emptyList())

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, mockLocalAiModels)
    }

    @Test
    fun `given repository has model list, then clear, expected receive valid list value, then empty list value`() {
        val stubObserver = useCase().test()

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, mockLocalAiModels)

        stubLocalModels.onNext(emptyList())

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, emptyList())
    }

    @Test
    fun `given repository has model list, then list changes, expected receive valid list value, then changed list value`() {
        val stubObserver = useCase().test()

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, mockLocalAiModels)

        val changedLocalAiModels = listOf(LocalAiModel.CustomOnnx)
        stubLocalModels.onNext(changedLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, changedLocalAiModels)
    }

    @Test
    fun `given repository observer has model list, emits twice, expected receive valid list value once`() {
        val stubObserver = useCase().test()

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, mockLocalAiModels)

        stubLocalModels.onNext(mockLocalAiModels)

        stubObserver
            .assertNoErrors()
            .assertValueCount(1)
    }

    @Test
    fun `given observer terminates with unexpected error, expected receive error value`() {
        val stubException = Throwable("Unexpected Flowable termination.")

        whenever(stubRepository.observeAllOnnx())
            .thenReturn(Flowable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
