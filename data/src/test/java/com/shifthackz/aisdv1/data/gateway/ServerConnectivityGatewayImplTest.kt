package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ServerConnectivityGatewayImplTest {

    private val stubException = Throwable("Internal server error.")
    private val stubConnectivityState = BehaviorSubject.create<Boolean>()
    private val stubConnectivityMonitor = mockk< ConnectivityMonitor>()
    private val stubServerUrlProvider = mockk<ServerUrlProvider>()

    private val gateway = ServerConnectivityGatewayImpl(
        connectivityMonitor = stubConnectivityMonitor,
        serverUrlProvider = stubServerUrlProvider,
    )

    @Before
    fun initialize() {
        every {
            stubServerUrlProvider(any())
        } returns Single.just("http://192.168.0.1:7860")

        every {
            stubConnectivityMonitor.observe(any())
        } returns stubConnectivityState
    }

    @Test
    fun `given initially offline, then go online, expected false, then true`() {
        val stubObserver = gateway.observe().test()

        stubConnectivityState.onNext(false)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, false)

        stubConnectivityState.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, true)
    }

    @Test
    fun `given initially online, then go offline, expected true, then false`() {
        val stubObserver = gateway.observe().test()

        stubConnectivityState.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, true)

        stubConnectivityState.onNext(false)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, false)
    }

    @Test
    fun `given received online signal twice, expected true, twice`() {
        val stubObserver = gateway.observe().test()

        stubConnectivityState.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, true)

        stubConnectivityState.onNext(true)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, true)
    }

    @Test
    fun `given received offline signal twice, expected false, twice`() {
        val stubObserver = gateway.observe().test()

        stubConnectivityState.onNext(false)

        stubObserver
            .assertNoErrors()
            .assertValueAt(0, false)

        stubConnectivityState.onNext(false)

        stubObserver
            .assertNoErrors()
            .assertValueAt(1, false)
    }

    @Test
    fun `given connectivity monitor throws error, expected error value`() {
        every {
            stubConnectivityMonitor.observe(any())
        } returns Observable.error(stubException)

        gateway.observe().test()
            .assertError(stubException)
            .assertNoValues()
            .await()
            .assertNotComplete()
    }
}
