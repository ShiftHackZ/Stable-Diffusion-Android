package com.shifthackz.aisdv1.core.imageprocessing.di

import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.imageprocessing.R
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val imageProcessingModule = module {

    factory {
        Base64ToBitmapConverter(
            get<SchedulersProvider>().computation,
            BitmapFactory.decodeResource(androidContext().resources, R.drawable.ic_broken),
        )
    }

    factory {
        BitmapToBase64Converter(get<SchedulersProvider>().computation)
    }

    factory {
        Base64EncodingConverter(get<SchedulersProvider>().computation)
    }
}
