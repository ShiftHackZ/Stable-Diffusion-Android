package com.shifthackz.aisdv1.feature.coreml.di

import org.koin.core.module.Module

/**
 * Exposes the `coreMlModule` value used by the SDAI Core ML feature layer.
 *
 * @author Dmitriy Moroz
 */
val coreMlModule: Module = platformCoreMlModule()

/**
 * Executes the `platformCoreMlModule` step in the SDAI Core ML feature layer.
 *
 * @return Result produced by `platformCoreMlModule`.
 * @author Dmitriy Moroz
 */
internal expect fun platformCoreMlModule(): Module
