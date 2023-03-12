package com.shifthackz.aisdv1.network.connectivity

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class ConnectivityMonitor {

    fun observe(serverUrl: String): Observable<Boolean> = Observable
        .interval(CONFIG_INTERVAL_PING, TimeUnit.MILLISECONDS)
        .flatMap {
            Observable.create { emitter ->
                val connection = URL(serverUrl).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                try {
                    val code = connection.responseCode

                    if (code == 200) {
                        emitter.onNext(true)
                    } else {
                        emitter.onNext(false)
                    }
                } catch (e: Exception) {
                    emitter.onNext(false)
                }
            }
        }
        .doAfterNext { state -> logConnection(serverUrl, state) }

    private fun logConnection(url: String, isConnected: Boolean) {
        val message = "$url --> ${if (isConnected) "✅ CONNECTED" else "️❌ DISCONNECTED"}"
        Log.d(ConnectivityMonitor::class.simpleName, message)
    }

    companion object {
        private const val CONFIG_INTERVAL_PING = 5_000L
    }
}
