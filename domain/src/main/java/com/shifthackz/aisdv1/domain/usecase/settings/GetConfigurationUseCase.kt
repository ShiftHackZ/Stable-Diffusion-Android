package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.ServerSource
import io.reactivex.rxjava3.core.Single

interface GetConfigurationUseCase {
    operator fun invoke(): Single<Configuration>

    data class Configuration(
        val serverUrl: String,
        val demoMode: Boolean,
        val source: ServerSource,
    )
}
