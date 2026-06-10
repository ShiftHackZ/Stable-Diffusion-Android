package com.shifthackz.aisdv1.network.connectivity

import com.shifthackz.aisdv1.network.client.createConfiguredHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.takeFrom
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Coordinates `ConnectivityMonitor` behavior in the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
class ConnectivityMonitor(
    /**
     * Exposes the `httpClient` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val httpClient: HttpClient = createConfiguredHttpClient(),
    /**
     * Exposes the `shouldSkipConnectionCheck` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    private val shouldSkipConnectionCheck: () -> Boolean = { false },
) {

    fun observe(serverUrl: String): Flow<Boolean> = flow {
        while (true) {
            delay(CONFIG_INTERVAL_PING)
            val state = checkConnection(serverUrl)
            emit(state)
        }
    }.catch {
        emit(false)
    }

    private suspend fun checkConnection(serverUrl: String): Boolean {
        if (shouldSkipConnectionCheck()) return true
        return try {
            httpClient.get {
                url.takeFrom(serverUrl)
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val CONFIG_INTERVAL_PING = 5_000L
    }
}
