package com.shifthackz.aisdv1.network.di

import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111GenerationApi
import com.shifthackz.aisdv1.network.api.automatic1111.KtorAutomatic1111GenerationApi
import com.shifthackz.aisdv1.network.api.automatic1111.KtorAutomatic1111MetadataApi
import com.shifthackz.aisdv1.network.api.horde.HordeGenerationApi
import com.shifthackz.aisdv1.network.api.horde.KtorHordeGenerationApi
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceGenerationApi
import com.shifthackz.aisdv1.network.api.huggingface.HuggingFaceModelsApi
import com.shifthackz.aisdv1.network.api.huggingface.KtorHuggingFaceGenerationApi
import com.shifthackz.aisdv1.network.api.huggingface.KtorHuggingFaceModelsApi
import com.shifthackz.aisdv1.network.api.imagecdn.ImageCdnApi
import com.shifthackz.aisdv1.network.api.imagecdn.KtorImageCdnApi
import com.shifthackz.aisdv1.network.api.openai.KtorOpenAiGenerationApi
import com.shifthackz.aisdv1.network.api.openai.OpenAiGenerationApi
import com.shifthackz.aisdv1.network.api.sdai.KtorSdaiAppApi
import com.shifthackz.aisdv1.network.api.sdai.SdaiAppApi
import com.shifthackz.aisdv1.network.api.stabilityai.KtorStabilityAiGenerationApi
import com.shifthackz.aisdv1.network.api.stabilityai.KtorStabilityAiEnginesApi
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiGenerationApi
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiEnginesApi
import com.shifthackz.aisdv1.network.api.swarmui.KtorSwarmUiGenerationApi
import com.shifthackz.aisdv1.network.api.swarmui.KtorSwarmUiModelsApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiGenerationApi
import com.shifthackz.aisdv1.network.api.swarmui.SwarmUiModelsApi
import org.koin.dsl.module

val coreNetworkModule = module {
    single<SdaiAppApi> {
        KtorSdaiAppApi(
            appBaseUrl = STABLE_DIFFUSION_APP_API_URL,
            reportBaseUrl = STABLE_DIFFUSION_REPORT_API_URL,
        )
    }
    single<ImageCdnApi> {
        KtorImageCdnApi(IMAGE_CDN_API_URL)
    }
    single<StabilityAiEnginesApi> {
        KtorStabilityAiEnginesApi(STABILITY_AI_API_URL)
    }
    single<StabilityAiGenerationApi> {
        KtorStabilityAiGenerationApi(STABILITY_AI_API_URL)
    }
    single<OpenAiGenerationApi> {
        KtorOpenAiGenerationApi(OPEN_AI_API_URL)
    }
    single<HordeGenerationApi> {
        KtorHordeGenerationApi(HORDE_AI_API_URL)
    }
    single<HuggingFaceGenerationApi> {
        KtorHuggingFaceGenerationApi(HUGGING_FACE_API_URL, HUGGING_FACE_INFERENCE_API_URL)
    }
    single<HuggingFaceModelsApi> {
        KtorHuggingFaceModelsApi(HUGGING_FACE_API_URL)
    }
    single<SwarmUiModelsApi> {
        KtorSwarmUiModelsApi()
    }
    single<SwarmUiGenerationApi> {
        KtorSwarmUiGenerationApi()
    }
    single<Automatic1111MetadataApi> {
        KtorAutomatic1111MetadataApi()
    }
    single<Automatic1111GenerationApi> {
        KtorAutomatic1111GenerationApi()
    }
}

private const val STABLE_DIFFUSION_APP_API_URL = "https://sdai.moroz.cc"
private const val STABLE_DIFFUSION_REPORT_API_URL = "https://sdai-report.moroz.cc"
private const val IMAGE_CDN_API_URL = "https://random.imagecdn.app"
private const val STABILITY_AI_API_URL = "https://api.stability.ai"
private const val OPEN_AI_API_URL = "https://api.openai.com"
private const val HORDE_AI_API_URL = "https://stablehorde.net"
private const val HUGGING_FACE_API_URL = "https://huggingface.co"
private const val HUGGING_FACE_INFERENCE_API_URL = "https://router.huggingface.co/hf-inference"
