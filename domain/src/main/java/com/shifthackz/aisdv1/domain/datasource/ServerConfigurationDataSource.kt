package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface ServerConfigurationDataSource {

    interface Remote : ServerConfigurationDataSource {
        fun fetchConfiguration(): Single<ServerConfiguration>
        fun updateConfiguration(configuration: ServerConfiguration): Completable
    }

    interface Local : ServerConfigurationDataSource {
        fun save(configuration: ServerConfiguration): Completable
        fun get(): Single<ServerConfiguration>
    }
}
