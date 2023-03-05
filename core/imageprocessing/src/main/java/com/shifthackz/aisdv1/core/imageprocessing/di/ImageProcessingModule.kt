package com.shifthackz.aisdv1.core.imageprocessing.di

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapProcessor
import org.koin.dsl.module

val imageProcessingModule = module {

    factory<Base64ToBitmapProcessor> {
        Base64ToBitmapConverter(get<SchedulersProvider>().computation)
    }
}
