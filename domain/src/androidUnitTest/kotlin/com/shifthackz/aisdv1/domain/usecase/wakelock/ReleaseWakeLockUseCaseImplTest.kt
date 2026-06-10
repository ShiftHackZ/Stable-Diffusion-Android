package com.shifthackz.aisdv1.domain.usecase.wakelock

import android.os.PowerManager
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ReleaseWakeLockUseCaseImplTest {

    private val stubException = Throwable("Can not release wakelock.")
    private val stubWakeLock = mock<PowerManager.WakeLock>()
    private val stubRepository = mock<WakeLockRepository>()

    private val useCase = ReleaseWakeLockUseCaseImpl(stubRepository)

    @Before
    fun initialize() {
        whenever(stubRepository.wakeLock)
            .thenReturn(stubWakeLock)
    }

    @Test
    fun `given wakelock was released successfully, expected result success`() {
        doNothing()
            .whenever(stubWakeLock)
            .release()

        val expected = Result.success(Unit)
        val actual = useCase()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given wakelock release failed, expected result failure`() {
        given(stubWakeLock.release())
            .willAnswer { throw stubException }

        val expected = Result.failure<Unit>(stubException)
        val actual = useCase()
        Assert.assertEquals(expected, actual)
    }
}
