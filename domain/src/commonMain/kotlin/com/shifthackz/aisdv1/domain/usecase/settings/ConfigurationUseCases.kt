package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.ConfigurationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Implements `DefaultGetConfigurationUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultGetConfigurationUseCaseImpl(
    /**
     * Exposes the `configurationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val configurationStore: ConfigurationStore,
    /**
     * Exposes the `authorizationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
) : GetConfigurationUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): Configuration =
        configurationStore.getConfiguration(
            authCredentials = authorizationStore.getAuthorizationCredentials(),
        )
}

/**
 * Implements `DefaultSetServerConfigurationUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DefaultSetServerConfigurationUseCaseImpl(
    /**
     * Exposes the `configurationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val configurationStore: ConfigurationStore,
    /**
     * Exposes the `authorizationStore` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
    /**
     * Exposes the `preferenceManager` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : SetServerConfigurationUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(configuration: Configuration) {
        authorizationStore.storeAuthorizationCredentials(configuration.authCredentials)
        configurationStore.setConfiguration(configuration)
        preferenceManager.refresh()
    }
}
