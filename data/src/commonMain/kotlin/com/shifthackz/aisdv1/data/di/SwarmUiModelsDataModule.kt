package com.shifthackz.aisdv1.data.di

import com.shifthackz.aisdv1.data.local.SwarmUiModelsLocalDataSource
import com.shifthackz.aisdv1.data.repository.SwarmUiModelsRepositoryImpl
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val swarmUiModelsDataModule = module {
    factoryOf(::SwarmUiModelsLocalDataSource) bind SwarmUiModelsDataSource.Local::class
    factoryOf(::SwarmUiModelsRepositoryImpl) bind SwarmUiModelsRepository::class
}
