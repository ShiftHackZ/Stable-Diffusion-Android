package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.local.GenerationResultLocalDataSource
import com.shifthackz.aisdv1.data.local.ServerConfigurationLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionModelsLocalDataSource
import com.shifthackz.aisdv1.data.local.StableDiffusionSamplersLocalDataSource
import com.shifthackz.aisdv1.domain.datasource.GenerationResultDataSource
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import org.koin.dsl.module

val localDataSourceModule = module {

    factory<StableDiffusionModelsDataSource.Local> {
        StableDiffusionModelsLocalDataSource(get())
    }

    factory<StableDiffusionSamplersDataSource.Local> {
        StableDiffusionSamplersLocalDataSource(get())
    }

    factory<ServerConfigurationDataSource.Local> {
        ServerConfigurationLocalDataSource(get())
    }

    factory<GenerationResultDataSource.Local> {
        GenerationResultLocalDataSource(get())
    }
}
