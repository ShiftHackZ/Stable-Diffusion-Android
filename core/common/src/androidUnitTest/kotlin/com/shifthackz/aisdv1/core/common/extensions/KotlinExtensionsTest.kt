package com.shifthackz.aisdv1.core.common.extensions

import org.junit.Assert
import org.junit.Test

class KotlinExtensionsTest {

    @Test
    fun `given TestClass with null value, then applyIf with true predicate, expected test value changed`() {
        class TestClass {
            var testValue: String? = null
        }

        val instance = TestClass()
        val predicate = true
        instance.applyIf(predicate) {
            testValue = "5598"
        }

        val expected = "5598"
        val actual = instance.testValue
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `given TestClass with null value, then applyIf with false predicate, expected test value NOT changed`() {
        class TestClass {
            var testValue: String? = null
        }

        val instance = TestClass()
        val predicate = false
        instance.applyIf(predicate) {
            testValue = "5598"
        }

        val expected = null
        val actual = instance.testValue
        Assert.assertEquals(expected, actual)
    }
}
