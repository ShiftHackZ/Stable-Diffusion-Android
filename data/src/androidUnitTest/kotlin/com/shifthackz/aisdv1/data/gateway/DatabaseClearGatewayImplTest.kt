package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class DatabaseClearGatewayImplTest {

    private val stubException = Throwable("Error occurred.")
    private val stubGatewayClearCacheDb = mockk<GatewayClearCacheDb>()
    private val stubGatewayClearPersistentDb = mockk<GatewayClearPersistentDb>()

    private val gateway = DatabaseClearGatewayImpl(
        gatewayClearCacheDb = stubGatewayClearCacheDb,
        gatewayClearPersistentDb = stubGatewayClearPersistentDb,
    )

    @Test
    fun `given attempt to clearSessionScopeDb, operation succeed, expected complete value`() = runTest {
        coEvery {
            stubGatewayClearCacheDb()
        } returns Unit

        val actual = runCatching { gateway.clearSessionScopeDb() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to clearSessionScopeDb, operation failed, expected error value`() = runTest {
        coEvery {
            stubGatewayClearCacheDb()
        } throws stubException

        val actual = runCatching { gateway.clearSessionScopeDb() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }

    @Test
    fun `given attempt to clearStorageScopeDb, operation succeed, expected complete value`() = runTest {
        coEvery {
            stubGatewayClearPersistentDb()
        } returns Unit

        val actual = runCatching { gateway.clearStorageScopeDb() }

        Assert.assertTrue(actual.isSuccess)
    }

    @Test
    fun `given attempt to clearStorageScopeDb, operation failed, expected error value`() = runTest {
        coEvery {
            stubGatewayClearPersistentDb()
        } throws stubException

        val actual = runCatching { gateway.clearStorageScopeDb() }

        Assert.assertSame(stubException, actual.exceptionOrNull())
    }
}
