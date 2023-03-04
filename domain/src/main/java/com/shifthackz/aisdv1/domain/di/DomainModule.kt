package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCaseImpl
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

    factory<GetStableDiffusionModelsUseCase> {
        GetStableDiffusionModelsUseCaseImpl(get(), get())
    }

    factory<SelectStableDiffusionModelUseCase> {
        SelectStableDiffusionModelUseCaseImpl(get())
    }
}
