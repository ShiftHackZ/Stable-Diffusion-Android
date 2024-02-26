package com.shifthackz.aisdv1.data.di

import android.content.Context
import android.os.PowerManager
import com.shifthackz.aisdv1.data.repository.DownloadableModelRepositoryImpl
import com.shifthackz.aisdv1.data.repository.GenerationResultRepositoryImpl
import com.shifthackz.aisdv1.data.repository.HordeGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.LocalDiffusionGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.RandomImageRepositoryImpl
import com.shifthackz.aisdv1.data.repository.ServerConfigurationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionEmbeddingsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionHyperNetworksRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionLorasRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionModelsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionSamplersRepositoryImpl
import com.shifthackz.aisdv1.data.repository.TemporaryGenerationResultRepositoryImpl
import com.shifthackz.aisdv1.data.repository.WakeLockRepositoryImpl
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    single<WakeLockRepository> {
        WakeLockRepositoryImpl {
            androidContext().getSystemService(Context.POWER_SERVICE) as PowerManager
        }
    }

    singleOf(::TemporaryGenerationResultRepositoryImpl) bind TemporaryGenerationResultRepository::class
    factoryOf(::LocalDiffusionGenerationRepositoryImpl) bind LocalDiffusionGenerationRepository::class
    factoryOf(::HordeGenerationRepositoryImpl) bind HordeGenerationRepository::class
    factoryOf(::StableDiffusionGenerationRepositoryImpl) bind StableDiffusionGenerationRepository::class
    factoryOf(::StableDiffusionModelsRepositoryImpl) bind StableDiffusionModelsRepository::class
    factoryOf(::StableDiffusionSamplersRepositoryImpl) bind StableDiffusionSamplersRepository::class
    factoryOf(::StableDiffusionLorasRepositoryImpl) bind StableDiffusionLorasRepository::class
    factoryOf(::StableDiffusionHyperNetworksRepositoryImpl) bind StableDiffusionHyperNetworksRepository::class
    factoryOf(::StableDiffusionEmbeddingsRepositoryImpl) bind StableDiffusionEmbeddingsRepository::class
    factoryOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
    factoryOf(::GenerationResultRepositoryImpl) bind GenerationResultRepository::class
    factoryOf(::RandomImageRepositoryImpl) bind RandomImageRepository::class
    factoryOf(::DownloadableModelRepositoryImpl) bind DownloadableModelRepository::class
}
