package com.shifthackz.aisdv1.network.di

import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import org.koin.dsl.module

/**
 * Exposes the `networkModule` value used by the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
val networkModule = module {
    factory { params ->
        ConnectivityMonitor(shouldSkipConnectionCheck = params.get())
    }
}
