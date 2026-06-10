package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ServerConnectivityGatewayImplTest {

    private val stubException = Throwable("Internal server error.")
    private val stubConnectivityMonitor = mockk<ConnectivityMonitor>()
    private val stubServerUrlProvider = mockk<ServerUrlProvider>()

    private val gateway = ServerConnectivityGatewayImpl(
        connectivityMonitor = stubConnectivityMonitor,
        serverUrlProvider = stubServerUrlProvider,
    )

    @Before
    fun initialize() {
        coEvery {
            stubServerUrlProvider(any())
        } returns "http://192.168.0.1:7860"

        every {
            stubConnectivityMonitor.observe(any())
        } returns flowOf(false, true)
    }

    @Test
    fun `given initially offline, then go online, expected false, then true`() = runTest {
        val actual = gateway.observe().toList()

        Assert.assertEquals(listOf(false, true), actual)
    }

    @Test
    fun `given initially online, then go offline, expected true, then false`() = runTest {
        every {
            stubConnectivityMonitor.observe(any())
        } returns flowOf(true, false)

        val actual = gateway.observe().toList()

        Assert.assertEquals(listOf(true, false), actual)
    }

    @Test
    fun `given received online signal twice, expected true, twice`() = runTest {
        every {
            stubConnectivityMonitor.observe(any())
        } returns flowOf(true, true)

        val actual = gateway.observe().toList()

        Assert.assertEquals(listOf(true, true), actual)
    }

    @Test
    fun `given received offline signal twice, expected false, twice`() = runTest {
        every {
            stubConnectivityMonitor.observe(any())
        } returns flowOf(false, false)

        val actual = gateway.observe().toList()

        Assert.assertEquals(listOf(false, false), actual)
    }

    @Test
    fun `given connectivity monitor throws error, expected error value`() = runTest {
        every {
            stubConnectivityMonitor.observe(any())
        } returns flow { throw stubException }

        val actual = runCatching { gateway.observe().toList() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
