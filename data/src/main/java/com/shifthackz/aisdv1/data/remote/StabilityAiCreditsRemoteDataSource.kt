package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiApi
import io.reactivex.rxjava3.core.Single

internal class StabilityAiCreditsRemoteDataSource(
    private val api: StabilityAiApi,
) : StabilityAiCreditsDataSource.Remote {

    override fun fetch(): Single<Float> = api
        .fetchCredits()
        .map { it.credits ?: 0f }
}
