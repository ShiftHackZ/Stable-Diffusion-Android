package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ServerConfigurationRepository {
    fun fetchConfiguration(): Completable
    fun fetchAndGetConfiguration(): Single<ServerConfiguration>
    fun getConfiguration(): Single<ServerConfiguration>
    fun updateConfiguration(configuration: ServerConfiguration): Completable
}
