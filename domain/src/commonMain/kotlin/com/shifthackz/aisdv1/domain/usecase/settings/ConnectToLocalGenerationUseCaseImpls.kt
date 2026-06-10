package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource

internal class ConnectToLocalDiffusionUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToLocalDiffusionUseCase {

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

internal class ConnectToMediaPipeUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
) : ConnectToMediaPipeUseCase {

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
