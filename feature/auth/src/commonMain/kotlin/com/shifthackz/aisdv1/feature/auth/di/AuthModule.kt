package com.shifthackz.aisdv1.feature.auth.di

import org.koin.core.module.Module

/**
 * Exposes the `authModule` value used by the SDAI authentication feature layer.
 *
 * @author Dmitriy Moroz
 */
val authModule: Module = platformAuthModule()

/**
 * Executes the `platformAuthModule` step in the SDAI authentication feature layer.
 *
 * @return Result produced by `platformAuthModule`.
 * @author Dmitriy Moroz
 */
internal expect fun platformAuthModule(): Module
