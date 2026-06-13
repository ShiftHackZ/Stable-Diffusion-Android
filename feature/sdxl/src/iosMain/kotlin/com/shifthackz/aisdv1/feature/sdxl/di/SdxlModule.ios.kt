package com.shifthackz.aisdv1.feature.sdxl.di

import com.shifthackz.aisdv1.domain.feature.sdxl.StableDiffusionCpp
import com.shifthackz.aisdv1.feature.sdxl.StableDiffusionCppNoOp
import org.koin.dsl.module

internal actual fun platformSdxlModule() = module {
    single<StableDiffusionCpp> { StableDiffusionCppNoOp }
}
