package com.shifthackz.aisdv1.core.imageprocessing.di

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import org.koin.dsl.module

val imageProcessingModule = module {

    factory {
        Base64ToBitmapConverter(get<SchedulersProvider>().computation)
    }

    factory {
        BitmapToBase64Converter(get<SchedulersProvider>().computation)
    }
}
