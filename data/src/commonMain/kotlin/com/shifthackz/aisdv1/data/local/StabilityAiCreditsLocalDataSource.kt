package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import kotlinx.coroutines.flow.MutableStateFlow

internal class StabilityAiCreditsLocalDataSource(
    private val creditsState: MutableStateFlow<Float> = MutableStateFlow(0f),
) : StabilityAiCreditsDataSource.Local {

    override suspend fun get() = creditsState.value

    override suspend fun save(value: Float) {
        creditsState.value = value
    }

    override fun observe() = creditsState
}
