package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

internal class StabilityAiCreditsRepositoryImpl(
    private val remoteDataSource: StabilityAiCreditsRemoteDataSource,
    private val localDataSource: StabilityAiCreditsDataSource.Local,
    private val preferenceManager: PreferenceManager,
) : StabilityAiCreditsRepository {

    override suspend fun fetch() = checkServerSourceSuspend(
        onValid = {
            val credits = remoteDataSource.fetch(preferenceManager.stabilityAiApiKey)
            localDataSource.save(credits)
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    override suspend fun fetchAndGet() = checkServerSourceSuspend(
        onValid = {
            runCatching { fetch() }
            get()
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    override fun fetchAndObserve(): Flow<Float> = flow {
        if (!isStabilityAiSource()) throw wrongServerSourceSelected()
        runCatching { fetch() }
        emitAll(observe())
    }

    override suspend fun get() = checkServerSourceSuspend(
        onValid = {
            localDataSource.get()
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    override fun observe() = checkServerSource(
        onValid = {
            localDataSource.observe()
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    private fun <T> checkServerSource(
        onValid: () -> T,
        onNotValid: () -> T,
    ): T = when (preferenceManager.source) {
        ServerSource.STABILITY_AI -> onValid()
        else -> onNotValid()
    }

    private suspend fun <T> checkServerSourceSuspend(
        onValid: suspend () -> T,
        onNotValid: () -> T,
    ): T = when {
        isStabilityAiSource() -> onValid()
        else -> onNotValid()
    }

    private fun isStabilityAiSource() =
        preferenceManager.source == ServerSource.STABILITY_AI

    private fun wrongServerSourceSelected() =
        IllegalStateException("Wrong server source selected.")
}
