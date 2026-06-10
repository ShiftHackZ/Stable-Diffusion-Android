package com.shifthackz.aisdv1.network.di

import com.shifthackz.aisdv1.network.connectivity.ConnectivityMonitor
import org.koin.dsl.module

val networkModule = module {
    factory { params ->
        ConnectivityMonitor(shouldSkipConnectionCheck = params.get())
    }
}
