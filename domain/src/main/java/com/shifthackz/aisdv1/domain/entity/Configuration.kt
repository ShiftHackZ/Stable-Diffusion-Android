package com.shifthackz.aisdv1.domain.entity

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

data class Configuration(
    val serverUrl: String = "",
    val swarmUiUrl: String = "",
    val swarmUiModel: String = "",
    val demoMode: Boolean = false,
    val source: ServerSource = ServerSource.AUTOMATIC1111,
    val hordeApiKey: String = "",
    val openAiApiKey: String = "",
    val huggingFaceApiKey: String = "",
    val huggingFaceModel: String = "",
    val stabilityAiApiKey: String = "",
    val stabilityAiEngineId: String = "",
    val authCredentials: AuthorizationCredentials = AuthorizationCredentials.None,
    val localOnnxModelId: String = "",
    val localOnnxModelPath: String = "",
    val localMediaPipeModelId: String = "",
    val localMediaPipeModelPath: String = "",
)
