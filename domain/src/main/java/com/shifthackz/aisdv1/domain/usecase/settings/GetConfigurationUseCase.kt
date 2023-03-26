package com.shifthackz.aisdv1.domain.usecase.settings

import io.reactivex.rxjava3.core.Single

interface GetConfigurationUseCase {
    operator fun invoke(): Single<Configuration>

    data class Configuration(
        val serverUrl: String,
        val demoMode: Boolean,
        val cloudAiMode: Boolean,
    )
}
