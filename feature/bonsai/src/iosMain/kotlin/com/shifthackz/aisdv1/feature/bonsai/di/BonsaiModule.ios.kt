package com.shifthackz.aisdv1.feature.bonsai.di

import com.shifthackz.aisdv1.domain.feature.bonsai.BonsaiDiffusion
import com.shifthackz.aisdv1.feature.bonsai.BonsaiDiffusionImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Executes the `platformBonsaiModule` step in the SDAI Bonsai feature layer.
 *
 * @return Result produced by `platformBonsaiModule`.
 * @author Dmitriy Moroz
 */
internal actual fun platformBonsaiModule() = module {
    singleOf(::BonsaiDiffusionImpl) bind BonsaiDiffusion::class
}
