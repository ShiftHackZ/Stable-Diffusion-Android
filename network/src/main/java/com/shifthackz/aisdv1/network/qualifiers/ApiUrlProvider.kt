package com.shifthackz.aisdv1.network.qualifiers

interface ApiUrlProvider {
    val stableDiffusionAutomaticApiUrl: String
    val stableDiffusionAppApiUrl: String
    val stableDiffusionReportApiUrl: String
    val hordeApiUrl: String
    val imageCdnApiUrl: String
    val huggingFaceApiUrl: String
    val huggingFaceInferenceApiUrl: String
    val openAiApiUrl: String
    val stabilityAiApiUrl: String
}
