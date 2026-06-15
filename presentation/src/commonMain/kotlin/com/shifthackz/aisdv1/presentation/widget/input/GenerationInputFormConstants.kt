package com.shifthackz.aisdv1.presentation.widget.input

/**
 * Provides the `GenerationInputFormConstants` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
internal object GenerationInputFormConstants {
    /**
     * Exposes the `SUB_SEED_STRENGTH_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SUB_SEED_STRENGTH_MIN = 0f
    /**
     * Exposes the `SUB_SEED_STRENGTH_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SUB_SEED_STRENGTH_MAX = 1f

    /**
     * Exposes the `SAMPLING_STEPS_RANGE_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS_RANGE_MIN = 1
    /**
     * Exposes the `SAMPLING_STEPS_RANGE_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS_RANGE_MAX = 150
    /**
     * Exposes the `SAMPLING_STEPS_RANGE_STABILITY_AI_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS_RANGE_STABILITY_AI_MAX = 50
    /**
     * Exposes the `SAMPLING_STEPS_RANGE_ARLI_AI_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS_RANGE_ARLI_AI_MAX = 40
    /**
     * Exposes the `SAMPLING_STEPS_RANGE_FAL_AI_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS_RANGE_FAL_AI_MAX = 12
    /**
     * Exposes the `SAMPLING_STEPS_LOCAL_DIFFUSION_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val SAMPLING_STEPS_LOCAL_DIFFUSION_MAX = 50

    /**
     * Exposes the `BATCH_RANGE_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val BATCH_RANGE_MIN = 1
    /**
     * Exposes the `BATCH_RANGE_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val BATCH_RANGE_MAX = 20

    /**
     * Exposes the `CFG_SCALE_RANGE_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val CFG_SCALE_RANGE_MIN = 1
    /**
     * Exposes the `CFG_SCALE_RANGE_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val CFG_SCALE_RANGE_MAX = 35

    /**
     * Exposes the `HIRES_SCALE_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val HIRES_SCALE_MIN = 1f
    /**
     * Exposes the `HIRES_SCALE_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val HIRES_SCALE_MAX = 4f
    /**
     * Exposes the `HIRES_STEPS_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val HIRES_STEPS_MIN = 0
    /**
     * Exposes the `HIRES_STEPS_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val HIRES_STEPS_MAX = 150
    /**
     * Exposes the `DENOISING_STRENGTH_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val DENOISING_STRENGTH_MIN = 0f
    /**
     * Exposes the `DENOISING_STRENGTH_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val DENOISING_STRENGTH_MAX = 1f
    /**
     * Exposes the `ADETAILER_CONFIDENCE_MIN` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val ADETAILER_CONFIDENCE_MIN = 0.1f
    /**
     * Exposes the `ADETAILER_CONFIDENCE_MAX` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    const val ADETAILER_CONFIDENCE_MAX = 1f

    /**
     * Exposes the `sizes` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sizes = listOf("64", "128", "256", "320", "384", "448", "512")
}
