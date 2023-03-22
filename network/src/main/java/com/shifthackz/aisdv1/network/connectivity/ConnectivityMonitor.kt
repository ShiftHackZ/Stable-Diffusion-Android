package com.shifthackz.aisdv1.network.connectivity

import com.shifthackz.aisdv1.core.common.log.debugLog
import io.reactivex.rxjava3.core.Observable
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class ConnectivityMonitor {

    fun observe(serverUrl: String): Observable<Boolean> = Observable
        .interval(CONFIG_INTERVAL_PING, TimeUnit.MILLISECONDS)
        .flatMap {
            val connection = URL(serverUrl).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val result = try {
                connection.responseCode == 200
            } catch (e: Exception) {
                false
            }
            Observable.just(result)
        }
        .doAfterNext { state -> logConnection(serverUrl, state) }

    private fun logConnection(url: String, isConnected: Boolean) = debugLog(
        "$url --> ${if (isConnected) "✅ CONNECTED" else "️❌ DISCONNECTED"}"
    )

    companion object {
        private const val CONFIG_INTERVAL_PING = 5_000L
    }
}
