package com.shifthackz.aisdv1.data.preference

import org.junit.Assert
import org.junit.Test

class SessionPreferenceImplTest {

    private val sessionPreference = SessionPreferenceImpl()

    @Test
    fun `given user reads default coinsPerDay value, expected -1`() {
        Assert.assertEquals(-1, sessionPreference.coinsPerDay)
    }

    @Test
    fun `given user reads default coinsPerDay value, then changes it, expected -1, then changed value`() {
        Assert.assertEquals(-1, sessionPreference.coinsPerDay)
        sessionPreference.coinsPerDay = 5598
        Assert.assertEquals(5598, sessionPreference.coinsPerDay)
    }
}
