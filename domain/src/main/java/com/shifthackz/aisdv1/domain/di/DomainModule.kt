package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.interactor.StableDiffusionModelSelectionInteractor
import com.shifthackz.aisdv1.domain.interactor.StableDiffusionModelSelectionInteractorImpl
import com.shifthackz.aisdv1.domain.usecase.*
import org.koin.dsl.module

val domainModule = module {

    factory<TextToImageUseCase> {
        TextToImageUseCaseImpl(get())
    }

    factory<PingStableDiffusionServiceUseCase> {
        PingStableDiffusionServiceUseCaseImpl(get())
    }

    factory<DataPreLoaderUseCase> {
        DataPreLoaderUseCaseImpl(get(), get(), get())
    }

    factory<StableDiffusionModelSelectionInteractor> {
        StableDiffusionModelSelectionInteractorImpl(get(), get())
    }
}
