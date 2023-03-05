package com.shifthackz.aisdv1.core.imageprocessing.di

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.contract.RxImageProcessor
import org.koin.dsl.module

val imageProcessingModule = module {

    factory<RxImageProcessor<Base64ToBitmapConverter.Input, Base64ToBitmapConverter.Output>> {
        Base64ToBitmapConverter(get<SchedulersProvider>().computation)
    }
}
