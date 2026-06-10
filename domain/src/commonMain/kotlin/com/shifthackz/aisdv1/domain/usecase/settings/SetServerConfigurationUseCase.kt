package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.Configuration

interface SetServerConfigurationUseCase {
    suspend operator fun invoke(configuration: Configuration)
}
