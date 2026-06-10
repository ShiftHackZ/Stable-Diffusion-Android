package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHordeApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestHuggingFaceApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestOpenAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestStabilityAiApiKeyUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestSwarmUiConnectivityUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

internal class DefaultConnectToSwarmUiUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val testSwarmUiConnectivityUseCase: TestSwarmUiConnectivityUseCase,
) : ConnectToSwarmUiUseCase {

    override suspend fun invoke(
        url: String,
        credentials: AuthorizationCredentials,
    ): Result<Unit> {
        var configuration: Configuration? = null
        return try {
            withTimeout(CONNECTION_TIMEOUT_MILLIS) {
                val originalConfiguration = getConfigurationUseCase()
                configuration = originalConfiguration
                val newConfiguration = originalConfiguration.copy(
                    source = ServerSource.SWARM_UI,
                    swarmUiUrl = url,
                    authCredentials = credentials,
                )
                setServerConfigurationUseCase(newConfiguration)
                delay(CONNECTION_DELAY_MILLIS)
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

internal class DefaultConnectToHordeUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val testHordeApiKeyUseCase: TestHordeApiKeyUseCase,
) : ConnectToHordeUseCase {

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

internal class DefaultConnectToHuggingFaceUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val testHuggingFaceApiKeyUseCase: TestHuggingFaceApiKeyUseCase,
) : ConnectToHuggingFaceUseCase {

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

internal class DefaultConnectToOpenAiUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val testOpenAiApiKeyUseCase: TestOpenAiApiKeyUseCase,
) : ConnectToOpenAiUseCase {

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

internal class DefaultConnectToStabilityAiUseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val testStabilityAiApiKeyUseCase: TestStabilityAiApiKeyUseCase,
) : ConnectToStabilityAiUseCase {

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
        delay(CONNECTION_DELAY_MILLIS)
        requireRemoteValidApiKey(testApiKey())
        Result.success(Unit)
    } catch (t: Throwable) {
        restoreRemoteConfigurationAndFail(configuration, setServerConfigurationUseCase, t)
    }
}

private const val CONNECTION_TIMEOUT_MILLIS = 30_000L
private const val CONNECTION_DELAY_MILLIS = 3_000L
