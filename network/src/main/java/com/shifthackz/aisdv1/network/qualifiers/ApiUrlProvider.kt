package com.shifthackz.aisdv1.network.qualifiers

interface ApiUrlProvider {
    val stableDiffusionAutomaticApiUrl: String
    val stableDiffusionAppApiUrl: String
    val stableDiffusionCloudAiApiUrl: String
    val hordeApiUrl: String
    val imageCdnApiUrl: String
}
