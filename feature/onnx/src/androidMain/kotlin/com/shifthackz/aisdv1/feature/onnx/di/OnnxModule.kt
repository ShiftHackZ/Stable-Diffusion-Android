package com.shifthackz.aisdv1.feature.onnx.di

import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionImpl
import com.shifthackz.aisdv1.feature.onnx.ai.tokenizer.EnglishTextTokenizer
import com.shifthackz.aisdv1.feature.onnx.ai.tokenizer.LocalDiffusionTextTokenizer
import com.shifthackz.aisdv1.feature.onnx.ai.unet.UNet
import com.shifthackz.aisdv1.feature.onnx.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.onnx.environment.OrtEnvironmentProviderImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val onnxModule = module {
    singleOf(::UNet)
    singleOf(::EnglishTextTokenizer) bind LocalDiffusionTextTokenizer::class
    singleOf(::LocalDiffusionImpl) bind LocalDiffusion::class
    singleOf(::OrtEnvironmentProviderImpl) bind OrtEnvironmentProvider::class
}
