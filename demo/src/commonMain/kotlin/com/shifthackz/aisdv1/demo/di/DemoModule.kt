package com.shifthackz.aisdv1.demo.di

import com.shifthackz.aisdv1.demo.ImageToImageDemoImpl
import com.shifthackz.aisdv1.demo.TextToImageDemoImpl
import com.shifthackz.aisdv1.demo.serialize.DemoDataSerializer
import com.shifthackz.aisdv1.domain.demo.ImageToImageDemo
import com.shifthackz.aisdv1.domain.demo.TextToImageDemo
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Exposes the `demoModule` value used by the SDAI demo layer.
 *
 * @author Dmitriy Moroz
 */
val demoModule = module {
    singleOf(::DemoDataSerializer)
    factoryOf(::TextToImageDemoImpl) bind TextToImageDemo::class
    factoryOf(::ImageToImageDemoImpl) bind ImageToImageDemo::class
}
