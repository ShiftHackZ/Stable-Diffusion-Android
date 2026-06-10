package com.shifthackz.aisdv1.network.client

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

actual fun platformHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = OkHttp
