package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.shifthackz.aisdv1.storage.db.persistent.contract.BenchmarkResultContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.BenchmarkResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * Reads and writes persisted benchmark results.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface BenchmarkResultDao {

    /**
     * Loads the most recent benchmark result.
     *
     * @return latest result, or null when benchmark has not been run.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${BenchmarkResultContract.TABLE} ORDER BY ${BenchmarkResultContract.CREATED_AT} DESC LIMIT 1")
    suspend fun queryLatest(): BenchmarkResultEntity?

    /**
     * Observes the most recent benchmark result.
     *
     * @return flow with the latest result.
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${BenchmarkResultContract.TABLE} ORDER BY ${BenchmarkResultContract.CREATED_AT} DESC LIMIT 1")
    fun observeLatest(): Flow<BenchmarkResultEntity?>

    /**
     * Inserts a benchmark result.
     *
     * @param item result entity to persist.
     * @return inserted row id.
     * @author Dmitriy Moroz
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: BenchmarkResultEntity): Long

    /**
     * Replaces stale benchmark rows with the newest result snapshot.
     *
     * @param item result entity to persist.
     * @return inserted row id.
     * @author Dmitriy Moroz
     */
    @Transaction
    suspend fun replaceLatest(item: BenchmarkResultEntity): Long {
        deleteAll()
        return insert(item)
    }

    /**
     * Deletes stored benchmark rows before a fresh snapshot is saved.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${BenchmarkResultContract.TABLE}")
    suspend fun deleteAll()
}
