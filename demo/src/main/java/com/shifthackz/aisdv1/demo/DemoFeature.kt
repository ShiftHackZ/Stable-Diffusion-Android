package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random

internal interface DemoFeature<T> {

    val demoDataSerializer: DemoDataSerializer

    fun mapper(input: T, base64: String): AiGenerationResult

    fun execute(input: T): Single<AiGenerationResult> = Single
        .create { emitter ->
            runCatching {
                val images = demoDataSerializer.readDemoAssets()
                val index = Random.nextInt(0, images.size)
                images[index]
            }.fold(
                onSuccess = emitter::onSuccess,
                onFailure = emitter::onError,
            )
        }
        .map { base64 -> mapper(input, base64) }
        .delay(5L, TimeUnit.SECONDS)
}
