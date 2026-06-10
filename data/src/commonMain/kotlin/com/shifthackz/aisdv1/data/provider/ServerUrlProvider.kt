package com.shifthackz.aisdv1.data.provider

/**
 * Executes the `function` step in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ServerUrlProvider {
    suspend operator fun invoke(endpoint: String): String
}
