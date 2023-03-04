package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ServerConfigurationDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ServerConfigurationRepository {
    fun fetchConfiguration(): Completable
    fun fetchAndGetConfiguration(): Single<ServerConfigurationDomain>
    fun getConfiguration(): Single<ServerConfigurationDomain>
    fun updateConfiguration(configuration: ServerConfigurationDomain): Completable
}
