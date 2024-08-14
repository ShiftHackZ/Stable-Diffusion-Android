package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.reactivex.rxjava3.core.Completable

internal class SetServerConfigurationUseCaseImpl(
    private val preferenceManager: PreferenceManager,
    private val authorizationStore: AuthorizationStore,
) : SetServerConfigurationUseCase {

    override fun invoke(configuration: Configuration): Completable =
        Completable.fromAction {
            authorizationStore.storeAuthorizationCredentials(configuration.authCredentials)
            preferenceManager.source = configuration.source
            preferenceManager.automatic1111ServerUrl = configuration.serverUrl
            preferenceManager.swarmUiServerUrl = configuration.swarmUiUrl
            preferenceManager.swarmUiModel = configuration.swarmUiModel
            preferenceManager.demoMode = configuration.demoMode
            preferenceManager.hordeApiKey = configuration.hordeApiKey
            preferenceManager.openAiApiKey = configuration.openAiApiKey
            preferenceManager.huggingFaceApiKey = configuration.huggingFaceApiKey
            preferenceManager.huggingFaceModel = configuration.huggingFaceModel
            preferenceManager.stabilityAiApiKey = configuration.stabilityAiApiKey
            preferenceManager.stabilityAiEngineId = configuration.stabilityAiEngineId
            preferenceManager.localModelId = configuration.localModelId
            preferenceManager.localDiffusionCustomModelPath = configuration.localModelPath
        }
}
