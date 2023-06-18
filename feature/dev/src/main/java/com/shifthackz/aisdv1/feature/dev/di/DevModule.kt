package com.shifthackz.aisdv1.feature.dev.di

import com.shifthackz.aisdv1.domain.usecase.dev.SpawnGalleryPageUseCase
import com.shifthackz.aisdv1.feature.dev.SpawnGalleryPageUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val devModule = module {
    factoryOf(::SpawnGalleryPageUseCaseImpl) bind SpawnGalleryPageUseCase::class
}
