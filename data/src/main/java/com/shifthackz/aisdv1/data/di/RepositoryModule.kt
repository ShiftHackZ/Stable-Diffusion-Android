package com.shifthackz.aisdv1.data.di

import android.content.Context
import android.os.PowerManager
import com.shifthackz.aisdv1.data.repository.DownloadableModelRepositoryImpl
import com.shifthackz.aisdv1.data.repository.EmbeddingsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.GenerationResultRepositoryImpl
import com.shifthackz.aisdv1.data.repository.HordeGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.HuggingFaceGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.HuggingFaceModelsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.LocalDiffusionGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.LorasRepositoryImpl
import com.shifthackz.aisdv1.data.repository.MediaPipeGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.OpenAiGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.RandomImageRepositoryImpl
import com.shifthackz.aisdv1.data.repository.ReportRepositoryImpl
import com.shifthackz.aisdv1.data.repository.ServerConfigurationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StabilityAiCreditsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StabilityAiEnginesRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StabilityAiGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionHyperNetworksRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionModelsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.StableDiffusionSamplersRepositoryImpl
import com.shifthackz.aisdv1.data.repository.SupportersRepositoryImpl
import com.shifthackz.aisdv1.data.repository.SwarmUiGenerationRepositoryImpl
import com.shifthackz.aisdv1.data.repository.SwarmUiModelsRepositoryImpl
import com.shifthackz.aisdv1.data.repository.TemporaryGenerationResultRepositoryImpl
import com.shifthackz.aisdv1.data.repository.WakeLockRepositoryImpl
import com.shifthackz.aisdv1.domain.repository.DownloadableModelRepository
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import com.shifthackz.aisdv1.domain.repository.HordeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository
import com.shifthackz.aisdv1.domain.repository.LocalDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import com.shifthackz.aisdv1.domain.repository.MediaPipeGenerationRepository
import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository
import com.shifthackz.aisdv1.domain.repository.ReportRepository
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository
import com.shifthackz.aisdv1.domain.repository.StabilityAiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionGenerationRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import com.shifthackz.aisdv1.domain.repository.SupportersRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiGenerationRepository
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
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
    factoryOf(::MediaPipeGenerationRepositoryImpl) bind MediaPipeGenerationRepository::class
    factoryOf(::HordeGenerationRepositoryImpl) bind HordeGenerationRepository::class
    factoryOf(::HuggingFaceGenerationRepositoryImpl) bind HuggingFaceGenerationRepository::class
    factoryOf(::OpenAiGenerationRepositoryImpl) bind OpenAiGenerationRepository::class
    factoryOf(::SwarmUiGenerationRepositoryImpl) bind SwarmUiGenerationRepository::class
    factoryOf(::SwarmUiModelsRepositoryImpl) bind SwarmUiModelsRepository::class
    factoryOf(::StabilityAiGenerationRepositoryImpl) bind StabilityAiGenerationRepository::class
    factoryOf(::StabilityAiCreditsRepositoryImpl) bind StabilityAiCreditsRepository::class
    factoryOf(::StabilityAiEnginesRepositoryImpl) bind StabilityAiEnginesRepository::class
    factoryOf(::StableDiffusionGenerationRepositoryImpl) bind StableDiffusionGenerationRepository::class
    factoryOf(::StableDiffusionModelsRepositoryImpl) bind StableDiffusionModelsRepository::class
    factoryOf(::StableDiffusionSamplersRepositoryImpl) bind StableDiffusionSamplersRepository::class
    factoryOf(::LorasRepositoryImpl) bind LorasRepository::class
    factoryOf(::StableDiffusionHyperNetworksRepositoryImpl) bind StableDiffusionHyperNetworksRepository::class
    factoryOf(::EmbeddingsRepositoryImpl) bind EmbeddingsRepository::class
    factoryOf(::ServerConfigurationRepositoryImpl) bind ServerConfigurationRepository::class
    factoryOf(::GenerationResultRepositoryImpl) bind GenerationResultRepository::class
    factoryOf(::RandomImageRepositoryImpl) bind RandomImageRepository::class
    factoryOf(::DownloadableModelRepositoryImpl) bind DownloadableModelRepository::class
    factoryOf(::HuggingFaceModelsRepositoryImpl) bind HuggingFaceModelsRepository::class
    factoryOf(::SupportersRepositoryImpl) bind SupportersRepository::class
    factoryOf(::ReportRepositoryImpl) bind ReportRepository::class
}
