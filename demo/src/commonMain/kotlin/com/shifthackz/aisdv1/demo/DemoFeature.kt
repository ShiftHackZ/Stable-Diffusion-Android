package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.delay

internal interface DemoFeature<T> {

    val demoDataSerializer: DemoDataSerializer

    fun mapper(input: T, base64: String): AiGenerationResult

    suspend fun execute(input: T): AiGenerationResult {
        val image = demoDataSerializer.nextDemoAsset()
        delay(DEMO_DELAY_MS)
        return mapper(input, image)
    }

    companion object {
        private const val DEMO_DELAY_MS = 5_000L
    }
}
