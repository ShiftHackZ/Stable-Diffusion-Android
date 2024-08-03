package com.shifthackz.aisdv1.core.common.appbuild

import org.junit.Assert
import org.junit.Test

class BuildVersionTest {

    @Test
    fun `Parse 1_0_0, expected success`() {
        val raw = "1.0.0"
        val version = BuildVersion(raw)
        Assert.assertEquals(true, raw == "$version")
    }

    @Test
    fun `Parse 1_0_0-alpha, expected success`() {
        val raw = "1.0.0-alpha"
        val version = BuildVersion(raw)
        Assert.assertEquals(true, raw == "$version")
    }

    @Test
    fun `Compare 1_0_0 and 1_0_0, expect equal`() {
        val version1 = BuildVersion(1, 0, 0)
        val version2 = BuildVersion(1, 0, 0)
        Assert.assertEquals(true, version1 == version2)
        Assert.assertEquals(0, version1.compareTo(version2))
    }

    @Test
    fun `Compare 1_0_0 and 1_0_1, expect 1_0_0 less than 1_0_1`() {
        val version1 = BuildVersion(1, 0, 0)
        val version2 = BuildVersion(1, 0, 1)
        Assert.assertEquals(true, version1 != version2)
        Assert.assertEquals(true, version1 < version2)
    }

    @Test
    fun `Compare 1_0_0 and 1_1_0, expect 1_0_0 less than 1_1_0`() {
        val version1 = BuildVersion(1, 0, 0)
        val version2 = BuildVersion(1, 1, 0)
        Assert.assertEquals(true, version1 != version2)
        Assert.assertEquals(true, version1 < version2)
    }

    @Test
    fun `Compare 1_0_0 and 2_0_0, expect 1_0_0 less than 2_0_0`() {
        val version1 = BuildVersion(1, 0, 0)
        val version2 = BuildVersion(2, 0, 0)
        Assert.assertEquals(true, version1 != version2)
        Assert.assertEquals(true, version1 < version2)
    }
}
