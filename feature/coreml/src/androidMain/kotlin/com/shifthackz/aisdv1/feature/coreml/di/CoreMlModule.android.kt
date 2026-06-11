package com.shifthackz.aisdv1.feature.coreml.di

import com.shifthackz.aisdv1.domain.feature.coreml.CoreMlDiffusion
import com.shifthackz.aisdv1.feature.coreml.CoreMlDiffusionImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Executes the `platformCoreMlModule` step in the SDAI Core ML feature layer.
 *
 * @return Result produced by `platformCoreMlModule`.
 * @author Dmitriy Moroz
 */
internal actual fun platformCoreMlModule() = module {
    singleOf(::CoreMlDiffusionImpl) bind CoreMlDiffusion::class
}
