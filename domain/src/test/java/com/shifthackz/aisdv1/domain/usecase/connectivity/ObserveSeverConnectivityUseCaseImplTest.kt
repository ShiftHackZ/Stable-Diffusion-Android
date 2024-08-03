package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ObserveSeverConnectivityUseCaseImplTest {

    private val stubException = Throwable("Unexpected Flowable termination.")
    private val stubConnectivityValue = BehaviorSubject.create<Boolean>()
    private val stubGateway = mock<ServerConnectivityGateway>()

    private val useCase = ObserveSeverConnectivityUseCaseImpl(stubGateway)

    @Before
    fun initialize() {
        whenever(stubGateway.observe())
            .thenReturn(stubConnectivityValue.toFlowable(BackpressureStrategy.LATEST))
    }

    @Test
    fun `given server not connected, then connection establishes, expected false, then true`() {
        val stubObserver = useCase().test()

        stubConnectivityValue.onNext(false)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, false)

        stubConnectivityValue.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, true)
    }

    @Test
    fun `given server connected, then connection lost, expected true, then false`() {
        val stubObserver = useCase().test()

        stubConnectivityValue.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, true)

        stubConnectivityValue.onNext(false)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, false)
    }

    @Test
    fun `given server connected, gateway emits value twice, expected true, only once`() {
        val stubObserver = useCase().test()

        stubConnectivityValue.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, true)

        stubConnectivityValue.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueCount(1)
    }

    @Test
    fun `given gateway throws unexpected flowable termination, expected error value`() {
        whenever(stubGateway.observe())
            .thenReturn(Flowable.error(stubException))

        useCase()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
