package com.shifthackz.aisdv1.domain.entity

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Carries `Configuration` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class Configuration(
    /**
     * Exposes the `serverUrl` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val serverUrl: String = "",
    /**
     * Exposes the `swarmUiUrl` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmUiUrl: String = "",
    /**
     * Exposes the `swarmUiModel` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmUiModel: String = "",
    /**
     * Exposes the `demoMode` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val demoMode: Boolean = false,
    /**
     * Exposes the `source` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    /**
     * Exposes the `hordeApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeApiKey: String = "",
    /**
     * Exposes the `openAiApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiApiKey: String = "",
    /**
     * Exposes the `huggingFaceApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceApiKey: String = "",
    /**
     * Exposes the `huggingFaceModel` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceModel: String = "",
    /**
     * Exposes the `stabilityAiApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiApiKey: String = "",
    /**
     * Exposes the `stabilityAiEngineId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAiEngineId: String = "",
    /**
     * Exposes the `falAiApiKey` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val falAiApiKey: String = "",
    /**
     * Exposes the `authCredentials` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val authCredentials: AuthorizationCredentials = AuthorizationCredentials.None,
    /**
     * Exposes the `localOnnxModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val localOnnxModelId: String = "",
    /**
     * Exposes the `localOnnxModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val localOnnxModelPath: String = "",
    /**
     * Exposes the `localMediaPipeModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val localMediaPipeModelId: String = "",
    /**
     * Exposes the `localMediaPipeModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val localMediaPipeModelPath: String = "",
    /**
     * Exposes the `localCoreMlModelId` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val localCoreMlModelId: String = "",
    /**
     * Exposes the `localCoreMlModelPath` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val localCoreMlModelPath: String = "",
)
