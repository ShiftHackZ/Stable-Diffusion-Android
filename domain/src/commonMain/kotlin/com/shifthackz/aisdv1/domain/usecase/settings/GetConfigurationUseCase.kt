package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration

interface GetConfigurationUseCase {
    suspend operator fun invoke(): Configuration
}
