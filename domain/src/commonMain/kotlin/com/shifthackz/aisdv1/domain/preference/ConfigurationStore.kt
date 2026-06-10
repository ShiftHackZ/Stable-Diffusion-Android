package com.shifthackz.aisdv1.domain.preference

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

interface ConfigurationStore {
    var automatic1111ServerUrl: String
    var swarmUiServerUrl: String
    var swarmUiModel: String
    var demoMode: Boolean
    var source: ServerSource
    var hordeApiKey: String
    var openAiApiKey: String
    var huggingFaceApiKey: String
    var huggingFaceModel: String
    var stabilityAiApiKey: String
    var stabilityAiEngineId: String
    var localOnnxModelId: String
    var localOnnxModelPath: String
    var localMediaPipeModelId: String
    var localMediaPipeModelPath: String

    fun getConfiguration(
        authCredentials: AuthorizationCredentials,
    ): Configuration = Configuration(
        serverUrl = automatic1111ServerUrl,
        swarmUiUrl = swarmUiServerUrl,
        swarmUiModel = swarmUiModel,
        demoMode = demoMode,
        source = source,
        hordeApiKey = hordeApiKey,
        openAiApiKey = openAiApiKey,
        huggingFaceApiKey = huggingFaceApiKey,
        huggingFaceModel = huggingFaceModel,
        stabilityAiApiKey = stabilityAiApiKey,
        stabilityAiEngineId = stabilityAiEngineId,
        authCredentials = authCredentials,
        localOnnxModelId = localOnnxModelId,
        localOnnxModelPath = localOnnxModelPath,
        localMediaPipeModelId = localMediaPipeModelId,
        localMediaPipeModelPath = localMediaPipeModelPath,
    )

    fun setConfiguration(configuration: Configuration) {
        source = configuration.source
        automatic1111ServerUrl = configuration.serverUrl
        swarmUiServerUrl = configuration.swarmUiUrl
        swarmUiModel = configuration.swarmUiModel
        demoMode = configuration.demoMode
        hordeApiKey = configuration.hordeApiKey
        openAiApiKey = configuration.openAiApiKey
        huggingFaceApiKey = configuration.huggingFaceApiKey
        huggingFaceModel = configuration.huggingFaceModel
        stabilityAiApiKey = configuration.stabilityAiApiKey
        stabilityAiEngineId = configuration.stabilityAiEngineId
        localOnnxModelId = configuration.localOnnxModelId
        localOnnxModelPath = configuration.localOnnxModelPath
        localMediaPipeModelId = configuration.localMediaPipeModelId
        localMediaPipeModelPath = configuration.localMediaPipeModelPath
    }
}
