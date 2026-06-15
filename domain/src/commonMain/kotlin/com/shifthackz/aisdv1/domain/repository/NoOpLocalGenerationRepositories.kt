package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Provides the `NoOpLocalDiffusionGenerationRepository` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpLocalDiffusionGenerationRepository : LocalDiffusionGenerationRepository {
    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<LocalDiffusionStatus> =
        flowOf(LocalDiffusionStatus(current = 0, total = 0))

    /**
     * Executes the `generateFromText` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("Local Diffusion generation is available on Android only.")

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interruptGeneration() = Unit
}

/**
 * Provides the `NoOpMediaPipeGenerationRepository` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpMediaPipeGenerationRepository : MediaPipeGenerationRepository {
    /**
     * Executes the `generateFromText` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("MediaPipe generation is available on Android only.")
}

/**
 * Provides the `NoOpStableDiffusionCppGenerationRepository` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpStableDiffusionCppGenerationRepository : StableDiffusionCppGenerationRepository {

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<LocalDiffusionStatus> =
        flowOf(LocalDiffusionStatus(current = 0, total = 0))

    /**
     * Executes the `generateFromText` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("Local SDXL stable-diffusion.cpp generation is available on Android only.")

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interruptGeneration() = Unit
}

/**
 * Provides the `NoOpCoreMlGenerationRepository` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpCoreMlGenerationRepository : CoreMlGenerationRepository {
    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<LocalDiffusionStatus> =
        flowOf(LocalDiffusionStatus(current = 0, total = 0))

    /**
     * Executes the `generateFromText` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("Silicon Diffusion Core ML generation is available on iOS only.")

    /**
     * Executes the `generateFromImage` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromImage(payload: ImageToImagePayload) =
        error("Silicon Diffusion Core ML generation is available on iOS only.")

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interruptGeneration() = Unit
}

/**
 * Provides the `NoOpBonsaiGenerationRepository` singleton used by the SDAI domain layer.
 *
 * @throws IllegalStateException when the current state is invalid.
 * @author Dmitriy Moroz
 */
object NoOpBonsaiGenerationRepository : BonsaiGenerationRepository {
    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @return Result produced by `observeStatus`.
     * @author Dmitriy Moroz
     */
    override fun observeStatus(): Flow<LocalDiffusionStatus> =
        flowOf(LocalDiffusionStatus(current = 0, total = 0))

    /**
     * Executes the `generateFromText` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("Bonsai Image generation is available on iOS only.")

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interruptGeneration() = Unit
}
