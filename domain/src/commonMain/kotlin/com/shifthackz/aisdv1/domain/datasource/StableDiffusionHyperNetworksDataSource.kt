package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface StableDiffusionHyperNetworksDataSource {

    interface Remote : StableDiffusionHyperNetworksDataSource {
        suspend fun fetchHyperNetworks(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): List<StableDiffusionHyperNetwork>
    }

    interface Local : StableDiffusionHyperNetworksDataSource {
        suspend fun getHyperNetworks(): List<StableDiffusionHyperNetwork>
        suspend fun insertHyperNetworks(list: List<StableDiffusionHyperNetwork>)
    }
}
