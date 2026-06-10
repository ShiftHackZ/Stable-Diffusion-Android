package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.ConfigurationStore

internal class DefaultTestConnectivityUseCaseImpl(
    private val remoteDataSource: StableDiffusionGenerationDataSource.Remote,
    private val authorizationStore: AuthorizationStore,
) : TestConnectivityUseCase {

    override suspend fun invoke(url: String) {
        remoteDataSource.checkAvailability(
            baseUrl = url,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
    }
}

internal class DefaultTestSwarmUiConnectivityUseCaseImpl(
    private val remoteDataSource: SwarmUiModelsRemoteDataSource,
    private val authorizationStore: AuthorizationStore,
) : TestSwarmUiConnectivityUseCase {

    override suspend fun invoke(url: String) {
        remoteDataSource.getNewSession(
            baseUrl = url,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
    }
}

internal class DefaultTestHordeApiKeyUseCaseImpl(
    private val configurationStore: ConfigurationStore,
    private val remoteDataSource: HordeGenerationDataSource.Remote,
) : TestHordeApiKeyUseCase {

    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.hordeApiKey)
}

internal class DefaultTestHuggingFaceApiKeyUseCaseImpl(
    private val configurationStore: ConfigurationStore,
    private val remoteDataSource: HuggingFaceGenerationDataSource.Remote,
) : TestHuggingFaceApiKeyUseCase {

    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.huggingFaceApiKey)
}

internal class DefaultTestOpenAiApiKeyUseCaseImpl(
    private val configurationStore: ConfigurationStore,
    private val remoteDataSource: OpenAiGenerationDataSource.Remote,
) : TestOpenAiApiKeyUseCase {

    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.openAiApiKey)
}

internal class DefaultTestStabilityAiApiKeyUseCaseImpl(
    private val configurationStore: ConfigurationStore,
    private val remoteDataSource: StabilityAiGenerationDataSource.Remote,
) : TestStabilityAiApiKeyUseCase {

    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.stabilityAiApiKey)
}
