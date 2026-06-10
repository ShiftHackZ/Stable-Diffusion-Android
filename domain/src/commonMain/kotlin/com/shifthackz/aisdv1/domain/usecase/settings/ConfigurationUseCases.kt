package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.ConfigurationStore

internal class DefaultGetConfigurationUseCaseImpl(
    private val configurationStore: ConfigurationStore,
    private val authorizationStore: AuthorizationStore,
) : GetConfigurationUseCase {

    override suspend fun invoke(): Configuration =
        configurationStore.getConfiguration(
            authCredentials = authorizationStore.getAuthorizationCredentials(),
        )
}

internal class DefaultSetServerConfigurationUseCaseImpl(
    private val configurationStore: ConfigurationStore,
    private val authorizationStore: AuthorizationStore,
) : SetServerConfigurationUseCase {

    override suspend fun invoke(configuration: Configuration) {
        authorizationStore.storeAuthorizationCredentials(configuration.authCredentials)
        configurationStore.setConfiguration(configuration)
    }
}
