package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.datasource.HordeGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.OpenAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionGenerationDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.ConfigurationStore

/**
 * Implements `DefaultTestConnectivityUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultTestConnectivityUseCaseImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StableDiffusionGenerationDataSource.Remote,
    /**
     * Exposes the `authorizationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
) : TestConnectivityUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(url: String) {
        remoteDataSource.checkAvailability(
            baseUrl = url,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
    }
}

/**
 * Implements `DefaultTestSwarmUiConnectivityUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultTestSwarmUiConnectivityUseCaseImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: SwarmUiModelsRemoteDataSource,
    /**
     * Exposes the `authorizationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
) : TestSwarmUiConnectivityUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(url: String) {
        remoteDataSource.getNewSession(
            baseUrl = url,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
    }
}

/**
 * Implements `DefaultTestHordeApiKeyUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultTestHordeApiKeyUseCaseImpl(
    /**
     * Exposes the `configurationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val configurationStore: ConfigurationStore,
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: HordeGenerationDataSource.Remote,
) : TestHordeApiKeyUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.hordeApiKey)
}

/**
 * Implements `DefaultTestHuggingFaceApiKeyUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultTestHuggingFaceApiKeyUseCaseImpl(
    /**
     * Exposes the `configurationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val configurationStore: ConfigurationStore,
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: HuggingFaceGenerationDataSource.Remote,
) : TestHuggingFaceApiKeyUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.huggingFaceApiKey)
}

/**
 * Implements `DefaultTestOpenAiApiKeyUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultTestOpenAiApiKeyUseCaseImpl(
    /**
     * Exposes the `configurationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val configurationStore: ConfigurationStore,
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: OpenAiGenerationDataSource.Remote,
) : TestOpenAiApiKeyUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.openAiApiKey)
}

/**
 * Implements `DefaultTestStabilityAiApiKeyUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultTestStabilityAiApiKeyUseCaseImpl(
    /**
     * Exposes the `configurationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val configurationStore: ConfigurationStore,
    /**
     * Exposes the `remoteDataSource` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StabilityAiGenerationDataSource.Remote,
) : TestStabilityAiApiKeyUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): Boolean =
        remoteDataSource.validateApiKey(configurationStore.stabilityAiApiKey)
}
