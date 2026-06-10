package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.gateway.mediastore.IosMediaStoreGateway
import com.shifthackz.aisdv1.domain.gateway.MediaStoreGateway
import org.koin.dsl.module

/**
 * Exposes the `iosDataOverridesModule` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
private val iosDataOverridesModule = module {
    single<MediaStoreGateway> {
        IosMediaStoreGateway()
    }
}

/**
 * Exposes the `dataModule` value used by the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
actual val dataModule = arrayOf(iosDataOverridesModule)
