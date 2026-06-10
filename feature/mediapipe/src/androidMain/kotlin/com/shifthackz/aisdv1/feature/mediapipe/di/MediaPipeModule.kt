package com.shifthackz.aisdv1.feature.mediapipe.di

import com.shifthackz.aisdv1.domain.feature.mediapipe.MediaPipe
import com.shifthackz.aisdv1.feature.mediapipe.MediaPipeImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Exposes the `mediaPipeModule` value used by the SDAI MediaPipe feature layer.
 *
 * @author Dmitriy Moroz
 */
val mediaPipeModule = module {
    factoryOf(::MediaPipeImpl) bind MediaPipe::class
}
