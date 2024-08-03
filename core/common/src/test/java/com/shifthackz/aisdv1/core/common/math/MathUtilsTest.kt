package com.shifthackz.aisdv1.core.common.math

import org.junit.Assert
import org.junit.Test

class MathUtilsTest {

    @Test
    fun `given Double with 8 fraction digits, then roundTo(2), expected Double with 2 fraction digits`() {
        val value = 55.98238462
        val expected = 55.98.toString()
        val actual = value.roundTo(2).toString()
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given Float with 6 fraction digits, then roundTo(2), expected Float with 2 fraction digits`() {
        val value = 55.982384f
        val expected = 55.98f.toString()
        val actual = value.roundTo(2).toString()
        Assert.assertEquals(expected, actual)
    }
}
