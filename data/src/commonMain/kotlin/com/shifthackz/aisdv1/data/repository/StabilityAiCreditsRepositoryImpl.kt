package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

/**
 * Implements `StabilityAiCreditsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StabilityAiCreditsRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StabilityAiCreditsRemoteDataSource,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: StabilityAiCreditsDataSource.Local,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : StabilityAiCreditsRepository {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetch() = checkServerSourceSuspend(
        onValid = {
            val credits = remoteDataSource.fetch(preferenceManager.stabilityAiApiKey)
            localDataSource.save(credits)
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    /**
     * Loads SDAI data through `fetchAndGet`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGet() = checkServerSourceSuspend(
        onValid = {
            runCatching { fetch() }
            get()
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    /**
     * Loads SDAI data through `fetchAndObserve`.
     *
     * @return Result produced by `fetchAndObserve`.
     * @author Dmitriy Moroz
     */
    override fun fetchAndObserve(): Flow<Float> = flow {
        if (!isStabilityAiSource()) throw wrongServerSourceSelected()
        runCatching { fetch() }
        emitAll(observe())
    }

    /**
     * Loads SDAI data through `get`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun get() = checkServerSourceSuspend(
        onValid = {
            localDataSource.get()
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    /**
     * Loads SDAI data through `observe`.
     *
     * @author Dmitriy Moroz
     */
    override fun observe() = checkServerSource(
        onValid = {
            localDataSource.observe()
        },
        onNotValid = {
            throw wrongServerSourceSelected()
        },
    )

    /**
     * Executes the `checkServerSource` step in the SDAI data layer.
     *
     * @param onValid callback invoked by the component.
     * @param onNotValid callback invoked by the component.
     * @author Dmitriy Moroz
     */
    private fun <T> checkServerSource(
        onValid: () -> T,
        onNotValid: () -> T,
    ): T = when (preferenceManager.source) {
        ServerSource.STABILITY_AI -> onValid()
        else -> onNotValid()
    }

    /**
     * Executes the `checkServerSourceSuspend` step in the SDAI data layer.
     *
     * @param onValid callback invoked by the component.
     * @param onNotValid callback invoked by the component.
     * @return Result produced by `checkServerSourceSuspend`.
     * @author Dmitriy Moroz
     */
    private suspend fun <T> checkServerSourceSuspend(
        onValid: suspend () -> T,
        onNotValid: () -> T,
    ): T = when {
        isStabilityAiSource() -> onValid()
        else -> onNotValid()
    }

    /**
     * Executes the `isStabilityAiSource` step in the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private fun isStabilityAiSource() =
        preferenceManager.source == ServerSource.STABILITY_AI

    /**
     * Executes the `wrongServerSourceSelected` step in the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private fun wrongServerSourceSelected() =
        IllegalStateException("Wrong server source selected.")
}
