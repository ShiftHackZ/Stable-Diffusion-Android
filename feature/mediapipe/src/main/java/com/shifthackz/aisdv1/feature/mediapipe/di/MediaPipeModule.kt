package com.shifthackz.aisdv1.feature.mediapipe.di

import com.shifthackz.aisdv1.domain.feature.mediapipe.MediaPipe
import com.shifthackz.aisdv1.feature.mediapipe.MediaPipeImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val mediaPipeModule = module {
    factoryOf(::MediaPipeImpl) bind MediaPipe::class
}
