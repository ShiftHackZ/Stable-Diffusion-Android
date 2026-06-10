package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

internal class DefaultConnectToA1111UseCaseImpl(
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val setServerConfigurationUseCase: SetServerConfigurationUseCase,
    private val testConnectivityUseCase: TestConnectivityUseCase,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
) : ConnectToA1111UseCase {

    override suspend fun invoke(
        url: String,
        isDemo: Boolean,
        credentials: AuthorizationCredentials,
    ): Result<Unit> {
        var configuration: Configuration? = null
        return try {
            withTimeout(CONNECTION_TIMEOUT_MILLIS) {
                val originalConfiguration = getConfigurationUseCase()
                configuration = originalConfiguration
                val newConfiguration = originalConfiguration.copy(
                    source = ServerSource.AUTOMATIC1111,
                    serverUrl = url,
                    demoMode = isDemo,
                    authCredentials = credentials,
                )
                setServerConfigurationUseCase(newConfiguration)
                withLocalNetworkPermissionRetry(url) {
                    testConnectivityUseCase(url)
                }
                delay(PRELOAD_DELAY_MILLIS)
                retryRemoteDelayed(PRELOAD_ATTEMPTS, PRELOAD_RETRY_DELAY_MILLIS) {
                    dataPreLoaderUseCase()
                }
            }
            Result.success(Unit)
        } catch (t: Throwable) {
            restoreRemoteConfigurationAndFail(configuration, setServerConfigurationUseCase, t)
        }
    }

    private companion object {
        const val CONNECTION_TIMEOUT_MILLIS = 30_000L
        const val PRELOAD_DELAY_MILLIS = 5_000L
        const val PRELOAD_RETRY_DELAY_MILLIS = 1_000L
        const val PRELOAD_ATTEMPTS = 3
    }
}
