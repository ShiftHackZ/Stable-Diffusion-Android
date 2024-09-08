package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.mocks.mockConfiguration
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Test

class GetConfigurationUseCaseImplTest {

    private val stubPreferenceManager = mockk<PreferenceManager>()
    private val stubAuthorizationStore = mockk<AuthorizationStore>()

    private val useCase = GetConfigurationUseCaseImpl(
        preferenceManager = stubPreferenceManager,
        authorizationStore = stubAuthorizationStore,
    )

    @Test
    fun `given configuration read success, expected valid configuration domain model value`() {
        every {
            stubAuthorizationStore.getAuthorizationCredentials()
        } returns AuthorizationCredentials.None

        every {
            stubPreferenceManager::automatic1111ServerUrl.get()
        } returns mockConfiguration.serverUrl

        every {
            stubPreferenceManager::swarmUiServerUrl.get()
        } returns mockConfiguration.swarmUiUrl

        every {
            stubPreferenceManager::swarmUiModel.get()
        } returns mockConfiguration.swarmUiModel

        every {
            stubPreferenceManager::demoMode.get()
        } returns mockConfiguration.demoMode

        every {
            stubPreferenceManager::source.get()
        } returns mockConfiguration.source

        every {
            stubPreferenceManager::hordeApiKey.get()
        } returns mockConfiguration.hordeApiKey

        every {
            stubPreferenceManager::openAiApiKey.get()
        } returns mockConfiguration.openAiApiKey

        every {
            stubPreferenceManager::huggingFaceApiKey.get()
        } returns mockConfiguration.huggingFaceApiKey

        every {
            stubPreferenceManager::huggingFaceModel.get()
        } returns mockConfiguration.huggingFaceModel

        every {
            stubPreferenceManager::stabilityAiApiKey.get()
        } returns mockConfiguration.stabilityAiApiKey

        every {
            stubPreferenceManager::stabilityAiEngineId.get()
        } returns mockConfiguration.stabilityAiEngineId

        every {
            stubPreferenceManager::localOnnxModelId.get()
        } returns mockConfiguration.localOnnxModelId

        every {
            stubPreferenceManager::localOnnxCustomModelPath.get()
        } returns mockConfiguration.localOnnxModelPath

        every {
            stubPreferenceManager::localMediaPipeModelId.get()
        } returns mockConfiguration.localMediaPipeModelId

        every {
            stubPreferenceManager::localMediaPipeCustomModelPath.get()
        } returns mockConfiguration.localMediaPipeModelPath

        useCase
            .invoke()
            .test()
            .assertNoErrors()
            .assertValue(mockConfiguration)
            .await()
            .assertComplete()
    }
}
