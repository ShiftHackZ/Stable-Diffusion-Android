package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ServerConfigurationDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface ServerConfigurationDataSource {

    interface Remote : ServerConfigurationDataSource {
        fun fetchConfiguration(): Single<ServerConfigurationDomain>
        fun updateConfiguration(configuration: ServerConfigurationDomain): Completable
    }

    interface Local : ServerConfigurationDataSource {
        fun save(configuration: ServerConfigurationDomain): Completable
        fun get(): Single<ServerConfigurationDomain>
    }
}
