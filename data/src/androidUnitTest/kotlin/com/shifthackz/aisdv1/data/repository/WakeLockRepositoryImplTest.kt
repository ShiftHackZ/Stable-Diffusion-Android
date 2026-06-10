package com.shifthackz.aisdv1.data.repository

import android.os.PowerManager
import io.mockk.every
import io.mockk.mockk
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
        val expected = stubWakeLock
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given repository already initialized, attempt to get wakelock, expected repository returns existing wakelock`() {
        every {
            stubPowerManager.newWakeLock(any(), any())
        } returns stubWakeLock

        val actualBeforeInit = repository.wakeLock
        val expectedBeforeInit = stubWakeLock
        Assert.assertEquals(expectedBeforeInit, actualBeforeInit)

        val actualAfterInit = repository.wakeLock
        val expectedAfterInit = stubWakeLock
        Assert.assertEquals(expectedAfterInit, actualAfterInit)

        Assert.assertEquals(actualBeforeInit, actualAfterInit)
    }
}
