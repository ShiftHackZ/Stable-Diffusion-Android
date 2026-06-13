package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.IosGenerationPlatformServices
import org.koin.dsl.module

/**
 * Exposes platform-specific presentation bindings.
 *
 * @author Dmitriy Moroz
 */
internal actual val platformPresentationModule = module {
    single<GenerationPlatformServices> { IosGenerationPlatformServices }
}
