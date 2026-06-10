package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.core.validation.di.validatorsModule
import com.shifthackz.aisdv1.data.di.coreDataModule
import com.shifthackz.aisdv1.data.di.dataModule
import com.shifthackz.aisdv1.demo.di.demoModule
import com.shifthackz.aisdv1.domain.di.coreDomainModule
import com.shifthackz.aisdv1.feature.auth.di.authModule
import com.shifthackz.aisdv1.network.di.coreNetworkModule
import com.shifthackz.aisdv1.network.di.networkModule
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

/**
 * Executes the `initKoin` step in the SDAI presentation layer.
 *
 * @return Result produced by `initKoin`.
 * @author Dmitriy Moroz
 */
fun initKoin(): Koin =
    KoinPlatform.getKoinOrNull() ?: startKoin {
        modules(
            coreNetworkModule,
            networkModule,
            validatorsModule,
            coreDomainModule,
            coreDataModule,
            *dataModule,
            demoModule,
            authModule,
            corePresentationModule,
        )
    }.koin
