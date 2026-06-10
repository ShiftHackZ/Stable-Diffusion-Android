package com.shifthackz.aisdv1.demo

import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import kotlinx.coroutines.delay

/**
 * Defines the `DemoFeature` contract for the SDAI demo layer.
 *
 * @author Dmitriy Moroz
 */
internal interface DemoFeature<T> {

    /**
     * Exposes the `demoDataSerializer` value used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    val demoDataSerializer: DemoDataSerializer

    /**
     * Converts SDAI data with `mapper`.
     *
     * @param input input value consumed by the API.
     * @param base64 Base64 image payload used by the operation.
     * @return Result produced by `mapper`.
     * @author Dmitriy Moroz
     */
    fun mapper(input: T, base64: String): AiGenerationResult

    /**
     * Executes the `execute` step in the SDAI demo layer.
     *
     * @param input input value consumed by the API.
     * @return Result produced by `execute`.
     * @author Dmitriy Moroz
     */
    suspend fun execute(input: T): AiGenerationResult {
        val image = demoDataSerializer.nextDemoAsset()
        delay(DEMO_DELAY_MS)
        return mapper(input, image)
    }

    /**
     * Provides the `companion object` singleton used by the SDAI demo layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `DEMO_DELAY_MS` value used by the SDAI demo layer.
         *
         * @author Dmitriy Moroz
         */
        private const val DEMO_DELAY_MS = 5_000L
    }
}
