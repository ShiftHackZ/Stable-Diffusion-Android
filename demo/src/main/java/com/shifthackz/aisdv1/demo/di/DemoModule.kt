package com.shifthackz.aisdv1.demo.di

import com.shifthackz.aisdv1.demo.ImageToImageDemoImpl
import com.shifthackz.aisdv1.demo.TextToImageDemoImpl
import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val demoModule = module {

    factory {
        DemoDataSerializer { androidApplication() }
    }

    factoryOf(::TextToImageDemoImpl) bind TextToImageDemo::class
    factoryOf(::ImageToImageDemoImpl) bind ImageToImageDemo::class
}
