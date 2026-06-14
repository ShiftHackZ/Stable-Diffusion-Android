package com.shifthackz.aisdv1.storage.db.persistent.dao

import androidx.room.Dao
import androidx.room.Query
import com.shifthackz.aisdv1.storage.db.persistent.contract.NetworkUsageContract
import com.shifthackz.aisdv1.storage.db.persistent.entity.NetworkUsageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Reads and writes persisted network usage counters.
 *
 * @author Dmitriy Moroz
 */
@Dao
interface NetworkUsageDao {

    /**
     * Observes all persisted counters so Settings updates without relying on lifecycle refreshes.
     *
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${NetworkUsageContract.TABLE}")
    fun observe(): Flow<List<NetworkUsageEntity>>

    /**
     * Reads all counters for one-shot repository snapshots.
     *
     * @author Dmitriy Moroz
     */
    @Query("SELECT * FROM ${NetworkUsageContract.TABLE}")
    suspend fun query(): List<NetworkUsageEntity>

    /**
     * Atomically creates or increments a bucket by [bytes].
     *
     * @param category Stable bucket key from the domain layer.
     * @param bytes Positive byte delta to add to the bucket.
     *
     * @author Dmitriy Moroz
     */
    @Query(
        """
        INSERT INTO ${NetworkUsageContract.TABLE}
            (${NetworkUsageContract.CATEGORY}, ${NetworkUsageContract.BYTES})
        VALUES (:category, :bytes)
        ON CONFLICT(${NetworkUsageContract.CATEGORY})
        DO UPDATE SET ${NetworkUsageContract.BYTES} = ${NetworkUsageContract.BYTES} + :bytes
        """,
    )
    suspend fun addBytes(category: String, bytes: Long)

    /**
     * Deletes every bucket when the user resets network usage statistics.
     *
     * @author Dmitriy Moroz
     */
    @Query("DELETE FROM ${NetworkUsageContract.TABLE}")
    suspend fun deleteAll()
}
