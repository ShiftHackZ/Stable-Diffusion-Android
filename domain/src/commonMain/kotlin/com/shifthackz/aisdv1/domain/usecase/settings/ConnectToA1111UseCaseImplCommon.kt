package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.TestConnectivityUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

/**
 * Implements `DefaultConnectToA1111UseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultConnectToA1111UseCaseImpl(
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
     * Exposes the `testConnectivityUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val testConnectivityUseCase: TestConnectivityUseCase,
    /**
     * Exposes the `dataPreLoaderUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
) : ConnectToA1111UseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param url remote URL used by the operation.
     * @param isDemo is demo value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
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

    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `CONNECTION_TIMEOUT_MILLIS` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        const val CONNECTION_TIMEOUT_MILLIS = 30_000L
        /**
         * Exposes the `PRELOAD_DELAY_MILLIS` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        const val PRELOAD_DELAY_MILLIS = 5_000L
        /**
         * Exposes the `PRELOAD_RETRY_DELAY_MILLIS` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        const val PRELOAD_RETRY_DELAY_MILLIS = 1_000L
        /**
         * Exposes the `PRELOAD_ATTEMPTS` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        const val PRELOAD_ATTEMPTS = 3
    }
}
