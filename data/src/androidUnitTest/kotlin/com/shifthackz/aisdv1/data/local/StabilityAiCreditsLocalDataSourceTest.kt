package com.shifthackz.aisdv1.data.local

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class StabilityAiCreditsLocalDataSourceTest {

    private val stubState = MutableStateFlow(0f)

    private val localDataSource = StabilityAiCreditsLocalDataSource(stubState)

    @Test
    fun `given attempt to get, then save and get, expected default value, save complete, then saved value`() = runTest {
        assertEquals(0f, localDataSource.get())

        localDataSource.save(5598f)

        assertEquals(5598f, localDataSource.get())
    }

    @Test
    fun `given attempt to observe changes, value changed from default to another, expected default value, save complete, then saved value`() = runTest {
        val defaultValue = localDataSource
            .observe()
            .take(1)
            .toList()

        localDataSource.save(5598f)
        val savedValue = localDataSource.observe().first()

        assertEquals(listOf(0f), defaultValue)
        assertEquals(5598f, savedValue)
    }
}
