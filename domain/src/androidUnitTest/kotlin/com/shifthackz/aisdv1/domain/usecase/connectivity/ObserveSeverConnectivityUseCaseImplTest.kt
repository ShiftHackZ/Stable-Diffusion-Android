package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.gateway.ServerConnectivityGateway
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ObserveSeverConnectivityUseCaseImplTest {

    private val stubException = Throwable("Unexpected Flow termination.")
    private val stubGateway = mock<ServerConnectivityGateway>()

    private val useCase = ObserveSeverConnectivityUseCaseImpl(stubGateway)

    @Before
    fun initialize() {
        whenever(stubGateway.observe())
            .thenReturn(flowOf(false, true))
    }

    @Test
    fun `given server not connected, then connection establishes, expected false, then true`() = runBlocking {
        val actual = useCase().toList()

        Assert.assertEquals(listOf(false, true), actual)
    }

    @Test
    fun `given server connected, then connection lost, expected true, then false`() = runBlocking {
        whenever(stubGateway.observe())
            .thenReturn(flowOf(true, false))

        val actual = useCase().toList()

        Assert.assertEquals(listOf(true, false), actual)
    }

    @Test
    fun `given server connected, gateway emits value twice, expected true, only once`() = runBlocking {
        whenever(stubGateway.observe())
            .thenReturn(flowOf(true, true))

        val actual = useCase().toList()

        Assert.assertEquals(listOf(true), actual)
    }

    @Test
    fun `given gateway throws unexpected flow termination, expected error value`() = runBlocking {
        whenever(stubGateway.observe())
            .thenReturn(flow { throw stubException })

        val actual = runCatching { useCase().toList() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
