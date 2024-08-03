package com.shifthackz.aisdv1.core.common.extensions

import org.junit.Assert
import org.junit.Test
import java.util.Date

class DateExtensionsTest {

    companion object {
        private val date = Date(894333955000) // 1998-05-05 05:05:55
    }

    @Test
    fun `given date 05_05_1998, then getRawDay, expected 5`() {
        val expected = 5
        val actual = date.getRawDay()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 05_05_1998, then getRawMonth, expected 5`() {
        val expected = 5
        val actual = date.getRawMonth()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 05_05_1998, then getRawYear, expected 1998`() {
        val expected = 1998
        val actual = date.getRawYear()
        Assert.assertEquals(expected, actual)
    }
}
