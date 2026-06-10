package com.shifthackz.aisdv1.presentation.di

/**
 * Exposes the `presentationModule` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val presentationModule = arrayOf(
    corePresentationModule,
    uiUtilsModule,
)
