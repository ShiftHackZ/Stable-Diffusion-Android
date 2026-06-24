package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.IosGenerationPlatformServices
import com.shifthackz.aisdv1.feature.sdxl.di.sdxlModule
import org.koin.dsl.module

/**
 * Exposes platform-specific presentation bindings.
 *
 * @author Dmitriy Moroz
 */
internal actual val platformPresentationModule = module {
    includes(sdxlModule)
    includes(optionalNonFreePresentationModules)

    single<GenerationPlatformServices> { IosGenerationPlatformServices }
}
