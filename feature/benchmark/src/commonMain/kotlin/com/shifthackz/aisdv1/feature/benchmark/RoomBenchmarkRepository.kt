package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.storage.db.persistent.dao.BenchmarkResultDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-backed benchmark repository.
 *
 * @author Dmitriy Moroz
 */
internal class RoomBenchmarkRepository(
    private val dao: BenchmarkResultDao,
) : BenchmarkRepository {

    override fun observeLatest(): Flow<BenchmarkResult?> =
        dao.observeLatest().map { entity -> entity?.toDomain() }

    override suspend fun getLatest(): BenchmarkResult? =
        dao.queryLatest()?.toDomain()

    override suspend fun save(result: BenchmarkResult): BenchmarkResult {
        val id = dao.replaceLatest(result.toEntity())
        return result.copy(id = id)
    }
}
