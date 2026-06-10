package com.shifthackz.aisdv1.core.common.extensions

import org.junit.Assert
import org.junit.Test

class StringExtensionsTest {

    @Test
    fun `given string with slash at and, expected no slash at end`() {
        val actual = "http://192.168.228.9:7860/".fixUrlSlashes()
        val expected = "http://192.168.228.9:7860"
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given string with 3 slashes at and, expected no slashes at end`() {
        val actual = "http://192.168.228.9:7860///".fixUrlSlashes()
        val expected = "http://192.168.228.9:7860"
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given string with even and odd duplicates and slash at end, expected no duplicate slashes and slash at end`() {
        val actual = "http://192.168.228.9:7860///path1////path2/".fixUrlSlashes()
        val expected = "http://192.168.228.9:7860/path1/path2"
        Assert.assertEquals(expected, actual)
    }
}
