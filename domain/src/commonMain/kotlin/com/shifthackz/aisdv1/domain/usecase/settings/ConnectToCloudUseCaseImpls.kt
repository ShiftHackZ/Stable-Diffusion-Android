package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestArliAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHuggingFaceApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestFalAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestOpenAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestStabilityAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestSwarmUiConnectivityUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds

/**
 * Implements `DefaultConnectToSwarmUiUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToSwarmUiUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testSwarmUiConnectivityUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testSwarmUiConnectivityUseCase: TestSwarmUiConnectivityUseCase,
) : ConnectToSwarmUiUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(
        url: String,
        credentials: AuthorizationCredentials,
    ): Result<Unit> {
        var configuration: Configuration? = null
        return try {
            withTimeout(CONNECTION_TIMEOUT_MILLIS.milliseconds) {
                val originalConfiguration = getConfigurationUseCase()
                configuration = originalConfiguration
                val newConfiguration = originalConfiguration.copy(
                    source = ServerSource.SWARM_UI,
                    swarmUiUrl = url,
                    authCredentials = credentials,
                )
                setServerConfigurationUseCase(newConfiguration)
                delay(CONNECTION_DELAY_MILLIS.milliseconds)
                withLocalNetworkPermissionRetry(url) {
                    testSwarmUiConnectivityUseCase(url)
                }
            }
            Result.success(Unit)
        } catch (t: Throwable) {
            restoreRemoteConfigurationAndFail(configuration, setServerConfigurationUseCase, t)
        }
    }
}

/**
 * Implements `DefaultConnectToHordeUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToHordeUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testHordeApiKeyUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testHordeApiKeyUseCase: TestHordeApiKeyUseCase,
) : ConnectToHordeUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String): Result<Unit> =
        connectWithApiKey(
            getConfigurationUseCase = getConfigurationUseCase,
            setServerConfigurationUseCase = setServerConfigurationUseCase,
            testApiKey = testHordeApiKeyUseCase::invoke,
        ) { configuration ->
            configuration.copy(
                source = ServerSource.HORDE,
                hordeApiKey = apiKey,
                authCredentials = AuthorizationCredentials.None,
            )
        }
}

/**
 * Implements `DefaultConnectToHuggingFaceUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToHuggingFaceUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testHuggingFaceApiKeyUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testHuggingFaceApiKeyUseCase: TestHuggingFaceApiKeyUseCase,
) : ConnectToHuggingFaceUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String, model: String): Result<Unit> =
        connectWithApiKey(
            getConfigurationUseCase = getConfigurationUseCase,
            setServerConfigurationUseCase = setServerConfigurationUseCase,
            testApiKey = testHuggingFaceApiKeyUseCase::invoke,
        ) { configuration ->
            configuration.copy(
                source = ServerSource.HUGGING_FACE,
                huggingFaceApiKey = apiKey,
                huggingFaceModel = model,
                authCredentials = AuthorizationCredentials.None,
            )
        }
}

/**
 * Implements `DefaultConnectToOpenAiUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToOpenAiUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testOpenAiApiKeyUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testOpenAiApiKeyUseCase: TestOpenAiApiKeyUseCase,
) : ConnectToOpenAiUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String): Result<Unit> =
        connectWithApiKey(
            getConfigurationUseCase = getConfigurationUseCase,
            setServerConfigurationUseCase = setServerConfigurationUseCase,
            testApiKey = testOpenAiApiKeyUseCase::invoke,
        ) { configuration ->
            configuration.copy(
                source = ServerSource.OPEN_AI,
                openAiApiKey = apiKey,
                authCredentials = AuthorizationCredentials.None,
            )
        }
}

/**
 * Implements `DefaultConnectToStabilityAiUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToStabilityAiUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testStabilityAiApiKeyUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testStabilityAiApiKeyUseCase: TestStabilityAiApiKeyUseCase,
) : ConnectToStabilityAiUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String): Result<Unit> =
        connectWithApiKey(
            getConfigurationUseCase = getConfigurationUseCase,
            setServerConfigurationUseCase = setServerConfigurationUseCase,
            testApiKey = testStabilityAiApiKeyUseCase::invoke,
        ) { configuration ->
            configuration.copy(
                source = ServerSource.STABILITY_AI,
                stabilityAiApiKey = apiKey,
                authCredentials = AuthorizationCredentials.None,
            )
        }
}

/**
 * Implements `DefaultConnectToFalAiUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToFalAiUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testFalAiApiKeyUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testFalAiApiKeyUseCase: TestFalAiApiKeyUseCase,
) : ConnectToFalAiUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String): Result<Unit> =
        connectWithApiKey(
            getConfigurationUseCase = getConfigurationUseCase,
            setServerConfigurationUseCase = setServerConfigurationUseCase,
            testApiKey = testFalAiApiKeyUseCase::invoke,
        ) { configuration ->
            configuration.copy(
                source = ServerSource.FAL_AI,
                falAiApiKey = apiKey,
                authCredentials = AuthorizationCredentials.None,
            )
        }
}

/**
 * Implements `DefaultConnectToArliAiUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToArliAiUseCaseImpl(
    /**
     * Exposes the `getConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val getConfigurationUseCase: GetConfigurationUseCase,
    /**
     * Exposes the `setServerConfigurationUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    /**
     * Exposes the `testArliAiApiKeyUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testArliAiApiKeyUseCase: TestArliAiApiKeyUseCase,
) : ConnectToArliAiUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(apiKey: String): Result<Unit> =
        connectWithApiKey(
            getConfigurationUseCase = getConfigurationUseCase,
            setServerConfigurationUseCase = setServerConfigurationUseCase,
            testApiKey = testArliAiApiKeyUseCase::invoke,
        ) { configuration ->
            configuration.copy(
                source = ServerSource.ARLI_AI,
                arliAiApiKey = apiKey,
                authCredentials = AuthorizationCredentials.None,
            )
        }
}

/**
 * Executes the `connectWithApiKey` step in the SDAI domain layer.
 *
 * @param getConfigurationUseCase get configuration use case value consumed by the API.
 * @param setServerConfigurationUseCase set server configuration use case value consumed by the API.
 * @param testApiKey test api key value consumed by the API.
 * @param updateConfiguration update configuration value consumed by the API.
 * @return Result produced by `connectWithApiKey`.
 * @author Dmitriy Moroz
 */
private suspend fun connectWithApiKey(
    getConfigurationUseCase: GetConfigurationUseCase,
    setServerConfigurationUseCase: SetServerConfigurationUseCase,
    testApiKey: suspend () -> Boolean,
    updateConfiguration: (Configuration) -> Configuration,
): Result<Unit> {
    var configuration: Configuration? = null
    return try {
        val originalConfiguration = getConfigurationUseCase()
        configuration = originalConfiguration
        setServerConfigurationUseCase(updateConfiguration(originalConfiguration))
        delay(CONNECTION_DELAY_MILLIS.milliseconds)
        requireRemoteValidApiKey(testApiKey())
        Result.success(Unit)
    } catch (t: Throwable) {
        restoreRemoteConfigurationAndFail(configuration, setServerConfigurationUseCase, t)
    }
}

/**
 * Exposes the `CONNECTION_TIMEOUT_MILLIS` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
private const val CONNECTION_TIMEOUT_MILLIS = 30_000L
/**
 * Exposes the `CONNECTION_DELAY_MILLIS` value used by the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
private const val CONNECTION_DELAY_MILLIS = 3_000L
