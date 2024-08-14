package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Single

internal class GetConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
    private val authorizationStore: AuthorizationStore,
) : GetConfigurationUseCase {

    override fun invoke(): Single<Configuration> = Single.just(
        Configuration(
            serverUrl = preferenceManager.automatic1111ServerUrl,
            swarmUiUrl = preferenceManager.swarmUiServerUrl,
            swarmUiModel = preferenceManager.swarmUiModel,
            demoMode = preferenceManager.demoMode,
            source = preferenceManager.source,
            hordeApiKey = preferenceManager.hordeApiKey,
            openAiApiKey = preferenceManager.openAiApiKey,
            huggingFaceApiKey = preferenceManager.huggingFaceApiKey,
            huggingFaceModel = preferenceManager.huggingFaceModel,
            stabilityAiApiKey = preferenceManager.stabilityAiApiKey,
            stabilityAiEngineId = preferenceManager.stabilityAiEngineId,
            authCredentials = authorizationStore.getAuthorizationCredentials(),
            localModelId = preferenceManager.localModelId,
            localModelPath = preferenceManager.localDiffusionCustomModelPath,
        )
    )
}
