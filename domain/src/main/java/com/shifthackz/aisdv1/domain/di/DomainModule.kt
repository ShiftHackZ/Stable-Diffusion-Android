package com.shifthackz.aisdv1.domain.di

import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCase
import com.shifthackz.aisdv1.domain.usecase.connectivity.PingStableDiffusionServiceUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.gallery.*
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.GetStableDiffusionModelsUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCase
import com.shifthackz.aisdv1.domain.usecase.sdmodel.SelectStableDiffusionModelUseCaseImpl
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCaseImpl
import org.koin.dsl.module

val domainModule = module {

    factory<TextToImageUseCase> {
        TextToImageUseCaseImpl(get())
    }

    factory<ImageToImageUseCase> {
        ImageToImageUseCaseImpl(get())
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

    factory<GetGalleryPageUseCase> {
        GetGalleryPageUseCaseImpl(get())
    }

    factory<GetAllGalleryUseCase> {
        GetAllGalleryUseCaseImpl(get())
    }

    factory<GetGalleryItemUseCase> {
        GetGalleryItemUseCaseImpl(get())
    }

    factory<DeleteGalleryItemUseCase> {
        DeleteGalleryItemUseCaseImpl(get())
    }

    factory<GetStableDiffusionSamplersUseCase> {
        GetStableDiffusionSamplersUseCaseImpl(get())
    }
}
