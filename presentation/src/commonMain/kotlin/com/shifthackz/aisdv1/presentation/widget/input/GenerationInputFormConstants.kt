package com.shifthackz.aisdv1.presentation.widget.input

internal object GenerationInputFormConstants {
    const val SUB_SEED_STRENGTH_MIN = 0f
    const val SUB_SEED_STRENGTH_MAX = 1f

    const val SAMPLING_STEPS_RANGE_MIN = 1
    const val SAMPLING_STEPS_RANGE_MAX = 150
    const val SAMPLING_STEPS_RANGE_STABILITY_AI_MAX = 50
    const val SAMPLING_STEPS_LOCAL_DIFFUSION_MAX = 50

    const val BATCH_RANGE_MIN = 1
    const val BATCH_RANGE_MAX = 20

    const val CFG_SCALE_RANGE_MIN = 1
    const val CFG_SCALE_RANGE_MAX = 35

    val sizes = listOf("64", "128", "256", "320", "384", "448", "512")
}
