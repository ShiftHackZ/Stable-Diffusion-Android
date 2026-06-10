package com.shifthackz.aisdv1.presentation.di

import org.koin.dsl.module


/**
 * Exposes the `corePresentationModule` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val corePresentationModule = module {
    registerPresentationCoreBindings()
    registerPresentationViewModelBindings()
}
