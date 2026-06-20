package com.shifthackz.aisdv1.domain.entity

/**
 * Runtime backend requested by Android Bonsai generation.
 *
 * `AUTO` lets the native runtime choose the safest available path. Explicit
 * values are kept in the domain payload so foreground generation, background
 * work, and persisted form state all pass the same backend key to the NDK layer.
 */
enum class BonsaiBackend(
    val key: String,
    val displayName: String,
) {
    AUTO("auto", "AUTO"),
    CPU("cpu", "CPU"),
    VULKAN("vulkan", "Vulkan"),
    ;

    companion object {
        fun parse(value: String?): BonsaiBackend {
            return entries.firstOrNull { backend ->
                backend.key == value || backend.name == value
            } ?: AUTO
        }
    }
}
