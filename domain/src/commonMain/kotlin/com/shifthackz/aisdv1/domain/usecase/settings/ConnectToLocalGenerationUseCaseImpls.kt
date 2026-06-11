package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Implements `ConnectToLocalDiffusionUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ConnectToLocalDiffusionUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToLocalDiffusionUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param modelId model id value consumed by the API.
     * @param modelPath model path value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(
        modelId: String,
        modelPath: String,
    ): Result<Unit> = runCatching {
        val originalConfiguration = getConfigurationUseCase()
        val newConfiguration = originalConfiguration.copy(
            source = ServerSource.LOCAL_MICROSOFT_ONNX,
            localOnnxModelId = modelId,
            localOnnxModelPath = modelPath,
        )
        setServerConfigurationUseCase(newConfiguration)
    }
}

/**
 * Implements `ConnectToMediaPipeUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ConnectToMediaPipeUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToMediaPipeUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param modelId model id value consumed by the API.
     * @param modelPath model path value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(
        modelId: String,
        modelPath: String,
    ): Result<Unit> = runCatching {
        val originalConfiguration = getConfigurationUseCase()
        val newConfiguration = originalConfiguration.copy(
            source = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
            localMediaPipeModelId = modelId,
            localMediaPipeModelPath = modelPath,
        )
        setServerConfigurationUseCase(newConfiguration)
    }
}

/**
 * Implements `ConnectToCoreMlUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ConnectToCoreMlUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToCoreMlUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param modelId model id value consumed by the API.
     * @param modelPath model path value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(
        modelId: String,
        modelPath: String,
    ): Result<Unit> = runCatching {
        val originalConfiguration = getConfigurationUseCase()
        val newConfiguration = originalConfiguration.copy(
            source = ServerSource.LOCAL_APPLE_CORE_ML,
            localCoreMlModelId = modelId,
            localCoreMlModelPath = modelPath,
        )
        setServerConfigurationUseCase(newConfiguration)
    }
}
