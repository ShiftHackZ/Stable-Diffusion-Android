package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.core.common.platform.Platform

interface ConnectToSdaiCloudUseCase {
    suspend operator fun invoke(platform: Platform, appVersion: String): Result<Unit>
}

object NoOpConnectToSdaiCloudUseCase : ConnectToSdaiCloudUseCase {
    override suspend fun invoke(platform: Platform, appVersion: String): Result<Unit> =
        Result.failure(UnsupportedOperationException("SDAI Cloud is unavailable in this build."))
}
