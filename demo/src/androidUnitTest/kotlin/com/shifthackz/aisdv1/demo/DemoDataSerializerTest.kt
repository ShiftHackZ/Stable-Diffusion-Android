package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DemoDataSerializerTest {

    private val serializer = DemoDataSerializer()

    @Test
    fun `given demo assets, expected about one hundred unique images`() {
        val assets = serializer.readDemoAssets()

        assertTrue(assets.size >= 100)
        assertEquals(assets.size, assets.toSet().size)
    }

    @Test
    fun `given demo assets, expected no repeated image before full cycle is consumed`() = runTest {
        val assets = serializer.readDemoAssets()

        val generated = assets.indices.map {
            serializer.nextDemoAsset()
        }

        assertEquals(assets.size, generated.toSet().size)
    }
}
