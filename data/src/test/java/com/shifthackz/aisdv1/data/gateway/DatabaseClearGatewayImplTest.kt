package com.shifthackz.aisdv1.data.gateway

import com.shifthackz.aisdv1.storage.gateway.GatewayClearCacheDb
import com.shifthackz.aisdv1.storage.gateway.GatewayClearPersistentDb
import io.mockk.every
import io.mockk.mockk
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
    fun `given attempt to clearSessionScopeDb, operation succeed, expected complete value`() {
        every {
            stubGatewayClearCacheDb()
        } returns Unit

        gateway
            .clearSessionScopeDb()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to clearSessionScopeDb, operation failed, expected error value`() {
        every {
            stubGatewayClearCacheDb()
        } throws stubException

        gateway
            .clearSessionScopeDb()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }

    @Test
    fun `given attempt to clearStorageScopeDb, operation succeed, expected complete value`() {
        every {
            stubGatewayClearPersistentDb()
        } returns Unit

        gateway
            .clearStorageScopeDb()
            .test()
            .assertNoErrors()
            .await()
            .assertComplete()
    }

    @Test
    fun `given attempt to clearStorageScopeDb, operation failed, expected error value`() {
        every {
            stubGatewayClearPersistentDb()
        } throws stubException

        gateway
            .clearStorageScopeDb()
            .test()
            .assertError(stubException)
            .await()
            .assertNotComplete()
    }
}
