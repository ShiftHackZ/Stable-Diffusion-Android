package com.shifthackz.aisdv1.domain.preference

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `ConfigurationStore` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ConfigurationStore {
    /**
     * Exposes the `automatic1111ServerUrl` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var automatic1111ServerUrl: String
    /**
     * Exposes the `swarmUiServerUrl` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var swarmUiServerUrl: String
    /**
     * Exposes the `swarmUiModel` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var swarmUiModel: String
    /**
     * Exposes the `demoMode` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var demoMode: Boolean
    /**
     * Exposes the `source` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var source: ServerSource
    /**
     * Exposes the `hordeApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var hordeApiKey: String
    /**
     * Exposes the `openAiApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var openAiApiKey: String
    /**
     * Exposes the `huggingFaceApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var huggingFaceApiKey: String
    /**
     * Exposes the `huggingFaceModel` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var huggingFaceModel: String
    /**
     * Exposes the `stabilityAiApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var stabilityAiApiKey: String
    /**
     * Exposes the `stabilityAiEngineId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var stabilityAiEngineId: String
    /**
     * Exposes the `falAiApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var falAiApiKey: String
    /**
     * Exposes the `localOnnxModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localOnnxModelId: String
    /**
     * Exposes the `localOnnxModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localOnnxModelPath: String
    /**
     * Exposes the `localMediaPipeModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localMediaPipeModelId: String
    /**
     * Exposes the `localMediaPipeModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localMediaPipeModelPath: String
    /**
     * Exposes the `localSdxlModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localSdxlModelId: String
    /**
     * Exposes the `localSdxlModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localSdxlModelPath: String
    /**
     * Exposes the `localCoreMlModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localCoreMlModelId: String
    /**
     * Exposes the `localCoreMlModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    var localCoreMlModelPath: String

    /**
     * Loads SDAI data through `getConfiguration`.
     *
     * @param authCredentials auth credentials value consumed by the API.
     * @author Dmitriy Moroz
     */
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
        falAiApiKey = falAiApiKey,
        authCredentials = authCredentials,
        localOnnxModelId = localOnnxModelId,
        localOnnxModelPath = localOnnxModelPath,
        localMediaPipeModelId = localMediaPipeModelId,
        localMediaPipeModelPath = localMediaPipeModelPath,
        localSdxlModelId = localSdxlModelId,
        localSdxlModelPath = localSdxlModelPath,
        localCoreMlModelId = localCoreMlModelId,
        localCoreMlModelPath = localCoreMlModelPath,
    )

    /**
     * Executes the `setConfiguration` step in the SDAI domain layer.
     *
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
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
        falAiApiKey = configuration.falAiApiKey
        localOnnxModelId = configuration.localOnnxModelId
        localOnnxModelPath = configuration.localOnnxModelPath
        localMediaPipeModelId = configuration.localMediaPipeModelId
        localMediaPipeModelPath = configuration.localMediaPipeModelPath
        localSdxlModelId = configuration.localSdxlModelId
        localSdxlModelPath = configuration.localSdxlModelPath
        localCoreMlModelId = configuration.localCoreMlModelId
        localCoreMlModelPath = configuration.localCoreMlModelPath
    }
}
