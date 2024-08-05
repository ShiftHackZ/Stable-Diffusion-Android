package com.shifthackz.aisdv1.data.preference

import org.junit.Assert
import org.junit.Test

class SessionPreferenceImplTest {

    private val sessionPreference = SessionPreferenceImpl()

    @Test
    fun `given user reads default swarmUiSessionId value, expected empty String`() {
        Assert.assertEquals("", sessionPreference.swarmUiSessionId)
    }

    @Test
    fun `given user reads default coinsPerDay value, then changes it, expected empty String, then changed value`() {
        Assert.assertEquals("", sessionPreference.swarmUiSessionId)
        sessionPreference.swarmUiSessionId = "5598"
        Assert.assertEquals("5598", sessionPreference.swarmUiSessionId)
    }
}
