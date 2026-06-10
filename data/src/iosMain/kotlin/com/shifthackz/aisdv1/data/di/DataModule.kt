package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.gateway.mediastore.IosMediaStoreGateway
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import org.koin.dsl.module

private val iosDataOverridesModule = module {
    single<MediaStoreGateway> {
        IosMediaStoreGateway()
    }
}

actual val dataModule = arrayOf(iosDataOverridesModule)
