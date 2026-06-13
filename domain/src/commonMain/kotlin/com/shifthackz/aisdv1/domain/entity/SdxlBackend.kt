package com.shifthackz.aisdv1.domain.entity

enum class SdxlBackend(
    val key: String,
    val displayName: String,
) {
    AUTO("auto", "AUTO"),
    CPU("cpu", "CPU"),
    OPEN_CL("opencl", "OpenCL"),
    VULKAN("vulkan", "Vulkan"),
    ;

    companion object {
        fun parse(value: String?): SdxlBackend {
            return entries.firstOrNull { backend ->
                backend.key == value || backend.name == value
            } ?: AUTO
        }
    }
}
