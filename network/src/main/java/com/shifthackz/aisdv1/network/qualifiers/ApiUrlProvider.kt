package com.shifthackz.aisdv1.network.qualifiers

interface ApiUrlProvider {
    val stableDiffusionAutomaticApiUrl: String
    val stableDiffusionAppUpdateApiUrl: String
    val stableDiffusionCloudAiApiUrl: String
}
