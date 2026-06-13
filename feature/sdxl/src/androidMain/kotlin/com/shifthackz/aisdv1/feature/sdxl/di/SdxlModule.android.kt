package com.shifthackz.aisdv1.feature.sdxl.di

import com.shifthackz.aisdv1.domain.feature.sdxl.StableDiffusionCpp
import com.shifthackz.aisdv1.feature.sdxl.AndroidStableDiffusionCppRuntime
import com.shifthackz.aisdv1.feature.sdxl.StableDiffusionCppImpl
import com.shifthackz.aisdv1.feature.sdxl.runtime.StableDiffusionCppRuntime
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformSdxlModule() = module {
    factoryOf(::AndroidStableDiffusionCppRuntime) bind StableDiffusionCppRuntime::class
    singleOf(::StableDiffusionCppImpl) bind StableDiffusionCpp::class
}
