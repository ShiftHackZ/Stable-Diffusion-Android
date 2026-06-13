package com.shifthackz.aisdv1.feature.sdxl.runtime

enum class StableDiffusionCppSampler(val key: String) {
    EULER_A("euler_a"),
    EULER("euler"),
    HEUN("heun"),
    DPM2("dpm2"),
    DPMPP2S_A("dpmpp2s_a"),
    DPMPP2M("dpmpp2m"),
    IPNDM("ipndm"),
    LCM("lcm"),
    DDIM_TRAILING("ddim_trailing"),
    TCD("tcd");

    companion object {
        fun parse(value: String): StableDiffusionCppSampler =
            entries.firstOrNull { sampler ->
                sampler.key == value.trim().lowercase().replace(" ", "_")
            } ?: EULER
    }
}
