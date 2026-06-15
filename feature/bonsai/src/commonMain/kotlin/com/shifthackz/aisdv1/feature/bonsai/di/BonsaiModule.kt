package com.shifthackz.aisdv1.feature.bonsai.di

import org.koin.core.module.Module

/**
 * Exposes the `bonsaiModule` value used by the SDAI Bonsai feature layer.
 *
 * @author Dmitriy Moroz
 */
val bonsaiModule: Module = platformBonsaiModule()

/**
 * Executes the `platformBonsaiModule` step in the SDAI Bonsai feature layer.
 *
 * @return Result produced by `platformBonsaiModule`.
 * @author Dmitriy Moroz
 */
internal expect fun platformBonsaiModule(): Module
