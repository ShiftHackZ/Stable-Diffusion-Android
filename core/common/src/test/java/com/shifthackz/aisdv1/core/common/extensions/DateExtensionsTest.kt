package com.shifthackz.aisdv1.core.common.extensions

import org.junit.Assert
import org.junit.Test
import java.util.Date

class DateExtensionsTest {

    companion object {
        private val date1 = Date(894333955000) // 1998-05-05 05:05:55
        private val date2 = Date(882137712000) // 1997-12-15 05:15:12
    }

    @Test
    fun `given date 05_05_1998, then getRawDay, expected 5`() {
        val expected = 5
        val actual = date1.getRawDay()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 12_15_1997, then getRawDay, expected 15`() {
        val expected = 15
        val actual = date2.getRawDay()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 05_05_1998, then getRawMonth, expected 5`() {
        val expected = 5
        val actual = date1.getRawMonth()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 12_15_1997, then getRawMonth, expected 12`() {
        val expected = 12
        val actual = date2.getRawMonth()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 05_05_1998, then getRawYear, expected 1998`() {
        val expected = 1998
        val actual = date1.getRawYear()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given date 12_15_1997, then getRawYear, expected 1997`() {
        val expected = 1997
        val actual = date2.getRawYear()
        Assert.assertEquals(expected, actual)
    }
}
