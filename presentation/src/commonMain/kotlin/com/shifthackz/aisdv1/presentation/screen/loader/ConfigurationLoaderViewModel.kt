package com.shifthackz.aisdv1.presentation.screen.loader

import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.mvi.BaseMviViewModel
import com.shifthackz.aisdv1.core.mvi.EmptyEffect
import com.shifthackz.aisdv1.core.mvi.EmptyIntent
import com.shifthackz.aisdv1.domain.entity.Configuration
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.presentation.navigation.router.ConfigurationLoaderRouter
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class ConfigurationLoaderViewModel(
    private val dispatchersProvider: DispatchersProvider,
    private val dataPreLoaderUseCase: DataPreLoaderUseCase,
    private val getConfigurationUseCase: GetConfigurationUseCase,
    private val router: ConfigurationLoaderRouter,
    private val onError: (Throwable) -> Unit = {},
) : BaseMviViewModel<ConfigurationLoaderState, EmptyIntent, EmptyEffect>(
    initialState = ConfigurationLoaderState(),
    effectDispatcher = dispatchersProvider.immediate,
) {

    init {
        launch(dispatchersProvider.io) {
            withContext(dispatchersProvider.immediate) {
                emitState(ConfigurationLoaderState(ConfigurationLoaderState.Status.Fetching))
            }

            runCatching {
                val configuration = getConfigurationUseCase()
                withContext(dispatchersProvider.immediate) {
                    emitState(ConfigurationLoaderState(ConfigurationLoaderState.Status.Launching))
                    router.navigateToHomeScreen()
                }
                if (!configuration.requiresRemotePreload()) return@runCatching
                withTimeout(STARTUP_PRELOAD_TIMEOUT_MILLIS) {
                    dataPreLoaderUseCase()
                }
            }.onFailure { t ->
                withContext(dispatchersProvider.immediate) {
                    emitState(ConfigurationLoaderState(ConfigurationLoaderState.Status.Failed))
                    router.navigateToHomeScreen()
                }
                onError(t)
            }
        }
    }

    override fun processIntent(intent: EmptyIntent) = Unit
}

private const val STARTUP_PRELOAD_TIMEOUT_MILLIS = 3_000L

private fun Configuration.requiresRemotePreload(): Boolean = when (source) {
    ServerSource.AUTOMATIC1111 -> serverUrl.isMobileRemoteEndpoint()
    ServerSource.SWARM_UI -> swarmUiUrl.isMobileRemoteEndpoint()
    ServerSource.HORDE,
    ServerSource.HUGGING_FACE,
    ServerSource.OPEN_AI,
    ServerSource.STABILITY_AI,
    ServerSource.LOCAL_MICROSOFT_ONNX,
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
    -> false
}

private fun String.isMobileRemoteEndpoint(): Boolean {
    val value = trim().lowercase()
    return value.isNotBlank() &&
        !value.startsWith("http://localhost") &&
        !value.startsWith("https://localhost") &&
        !value.startsWith("http://127.0.0.1") &&
        !value.startsWith("https://127.0.0.1") &&
        !value.startsWith("http://[::1]") &&
        !value.startsWith("https://[::1]")
}
