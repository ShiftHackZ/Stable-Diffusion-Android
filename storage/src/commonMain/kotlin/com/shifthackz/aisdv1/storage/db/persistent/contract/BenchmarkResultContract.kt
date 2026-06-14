package com.shifthackz.aisdv1.storage.db.persistent.contract

/**
 * Defines column names for persisted benchmark results.
 *
 * @author Dmitriy Moroz
 */
object BenchmarkResultContract {
    const val TABLE = "benchmark_result"
    const val ID = "id"
    const val CREATED_AT = "created_at"
    const val PLATFORM = "platform"
    const val MANUFACTURER = "manufacturer"
    const val MODEL = "model"
    const val OS_VERSION = "os_version"
    const val CPU_NAME = "cpu_name"
    const val CPU_CORES = "cpu_cores"
    const val GPU_NAME = "gpu_name"
    const val TOTAL_RAM_MB = "total_ram_mb"
    const val AVAILABLE_RAM_MB = "available_ram_mb"
    const val TOTAL_VRAM_MB = "total_vram_mb"
    const val AVAILABLE_VRAM_MB = "available_vram_mb"
    const val ACCELERATORS = "accelerators"
    const val CPU_SCORE = "cpu_score"
    const val MEMORY_SCORE = "memory_score"
    const val ACCELERATOR_SCORE = "accelerator_score"
    const val TOTAL_SCORE = "total_score"
    const val ESTIMATED_TIME_SECONDS = "estimated_time_seconds"
    const val RECOMMENDED_WIDTH = "recommended_width"
    const val RECOMMENDED_HEIGHT = "recommended_height"
    const val RECOMMENDED_STEPS = "recommended_steps"
    const val RECOMMENDED_CFG = "recommended_cfg"
    const val RECOMMENDED_BATCH = "recommended_batch"
    const val RECOMMENDED_PROVIDERS = "recommended_providers"
    const val RECOMMENDED_BACKGROUND = "recommended_background"
    const val RECOMMENDED_BACKEND = "recommended_backend"
    const val NOTES = "notes"
    const val CREATED_AT_INDEX = "index_benchmark_result_created_at"
}
