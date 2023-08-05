package com.shifthackz.aisdv1.feature.diffusion.di

import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionImpl
import com.shifthackz.aisdv1.feature.diffusion.ai.tokenizer.EnglishTextTokenizer
import com.shifthackz.aisdv1.feature.diffusion.ai.tokenizer.LocalDiffusionTextTokenizer
import com.shifthackz.aisdv1.feature.diffusion.ai.unet.UNet
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProviderImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diffusionModule = module {
    single { UNet(androidApplication()) }
    single<LocalDiffusionTextTokenizer> { EnglishTextTokenizer(androidApplication()) }
    singleOf(::LocalDiffusionImpl) bind LocalDiffusion::class
    singleOf(::OrtEnvironmentProviderImpl) bind OrtEnvironmentProvider::class
}
