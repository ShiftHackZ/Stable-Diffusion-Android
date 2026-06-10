package com.shifthackz.aisdv1.core.imageprocessing.di

import android.graphics.BitmapFactory
import com.shifthackz.aisdv1.core.imageprocessing.Base64EncodingConverter
import com.shifthackz.aisdv1.core.imageprocessing.Base64ToBitmapConverter
import com.shifthackz.aisdv1.core.imageprocessing.BitmapToBase64Converter
import com.shifthackz.aisdv1.core.imageprocessing.R
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Exposes the `imageProcessingModule` value used by the SDAI image processing layer.
 *
 * @author Dmitriy Moroz
 */
val imageProcessingModule = module {

    factory {
        Base64ToBitmapConverter(
            BitmapFactory.decodeResource(androidContext().resources, R.drawable.ic_broken),
        )
    }

    factory {
        BitmapToBase64Converter()
    }

    factory {
        Base64EncodingConverter()
    }
}
