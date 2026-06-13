package com.shifthackz.aisdv1.data.repository

import android.os.PowerManager
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test

class WakeLockRepositoryImplTest {

    private val stubWakeLock = mockk<PowerManager.WakeLock>()
    private val stubPowerManager = mockk<PowerManager>()

    private val repository = WakeLockRepositoryImpl { stubPowerManager }

    @Test
    fun `given repository is not yet initialized, attempt to get wakelock, expected repository initializes and returns wakelock`() {
        every {
            stubPowerManager.newWakeLock(any(), any())
        } returns stubWakeLock

        val actual = repository.wakeLock
        Assert.assertNotNull(actual)
    }

    @Test
    fun `given repository already initialized, attempt to get wakelock, expected repository returns existing wakelock`() {
        every {
            stubPowerManager.newWakeLock(any(), any())
        } returns stubWakeLock

        val actualBeforeInit = repository.wakeLock
        val actualAfterInit = repository.wakeLock

        Assert.assertEquals(actualBeforeInit, actualAfterInit)
    }

    @Test
    fun `given repository wake lock, acquire and release, expected calls delegated to android wake lock`() {
        every {
            stubPowerManager.newWakeLock(any(), any())
        } returns stubWakeLock
        every {
            stubWakeLock.acquire(any<Long>())
        } just Runs
        every {
            stubWakeLock.release()
        } just Runs

        repository.wakeLock.acquire(1000L)
        repository.wakeLock.release()

        verify {
            stubWakeLock.acquire(1000L)
            stubWakeLock.release()
        }
    }
}
