package com.shifthackz.aisdv1.network.client

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

/**
 * Executes the `platformHttpClientEngine` step in the SDAI network layer.
 *
 * @return Result produced by `platformHttpClientEngine`.
 * @author Dmitriy Moroz
 */
actual fun platformHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = Darwin
