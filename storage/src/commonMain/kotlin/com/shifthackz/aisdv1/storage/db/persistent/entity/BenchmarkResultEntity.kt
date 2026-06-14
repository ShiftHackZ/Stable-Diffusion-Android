package com.shifthackz.aisdv1.storage.db.persistent.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.shifthackz.aisdv1.storage.db.persistent.contract.BenchmarkResultContract

/**
 * Persisted snapshot of a safe hardware benchmark run.
 *
 * @author Dmitriy Moroz
 */
@Entity(
    tableName = BenchmarkResultContract.TABLE,
    indices = [
        Index(
            value = [BenchmarkResultContract.CREATED_AT],
            name = BenchmarkResultContract.CREATED_AT_INDEX,
        ),
    ],
)
data class BenchmarkResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = BenchmarkResultContract.ID)
    val id: Long,
    @ColumnInfo(name = BenchmarkResultContract.CREATED_AT)
    val createdAt: Long,
    @ColumnInfo(name = BenchmarkResultContract.PLATFORM)
    val platform: String,
    @ColumnInfo(name = BenchmarkResultContract.MANUFACTURER)
    val manufacturer: String,
    @ColumnInfo(name = BenchmarkResultContract.MODEL)
    val model: String,
    @ColumnInfo(name = BenchmarkResultContract.OS_VERSION)
    val osVersion: String,
    @ColumnInfo(name = BenchmarkResultContract.CPU_NAME)
    val cpuName: String,
    @ColumnInfo(name = BenchmarkResultContract.CPU_CORES)
    val cpuCores: Int,
    @ColumnInfo(name = BenchmarkResultContract.GPU_NAME)
    val gpuName: String,
    @ColumnInfo(name = BenchmarkResultContract.TOTAL_RAM_MB)
    val totalRamMb: Long,
    @ColumnInfo(name = BenchmarkResultContract.AVAILABLE_RAM_MB)
    val availableRamMb: Long,
    @ColumnInfo(name = BenchmarkResultContract.TOTAL_VRAM_MB)
    val totalVramMb: Long,
    @ColumnInfo(name = BenchmarkResultContract.AVAILABLE_VRAM_MB)
    val availableVramMb: Long,
    @ColumnInfo(name = BenchmarkResultContract.ACCELERATORS)
    val accelerators: String,
    @ColumnInfo(name = BenchmarkResultContract.CPU_SCORE)
    val cpuScore: Int,
    @ColumnInfo(name = BenchmarkResultContract.MEMORY_SCORE)
    val memoryScore: Int,
    @ColumnInfo(name = BenchmarkResultContract.ACCELERATOR_SCORE)
    val acceleratorScore: Int,
    @ColumnInfo(name = BenchmarkResultContract.TOTAL_SCORE)
    val totalScore: Int,
    @ColumnInfo(name = BenchmarkResultContract.ESTIMATED_TIME_SECONDS)
    val estimatedTimeSeconds: Int,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_WIDTH)
    val recommendedWidth: Int,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_HEIGHT)
    val recommendedHeight: Int,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_STEPS)
    val recommendedSteps: Int,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_CFG)
    val recommendedCfg: Float,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_BATCH)
    val recommendedBatch: Int,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_PROVIDERS)
    val recommendedProviders: String,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_BACKGROUND)
    val recommendedBackground: Boolean,
    @ColumnInfo(name = BenchmarkResultContract.RECOMMENDED_BACKEND)
    val recommendedBackend: String,
    @ColumnInfo(name = BenchmarkResultContract.NOTES)
    val notes: String,
)
