package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork

interface StableDiffusionHyperNetworksRepository {
    suspend fun fetchHyperNetworks()
    suspend fun fetchAndGetHyperNetworks(): List<StableDiffusionHyperNetwork>
    suspend fun getHyperNetworks(): List<StableDiffusionHyperNetwork>
}
