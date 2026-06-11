package com.shifthackz.aisdv1.domain.entity

/**
 * Coordinates A1111 scheduler selection in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class Scheduler(
    /**
     * Exposes the `alias` value used by the A1111 API.
     *
     * @author Dmitriy Moroz
     */
    val alias: String,
    /**
     * Exposes the `displayName` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val displayName: String,
) {
    AUTOMATIC("automatic", "Automatic"),
    UNIFORM("uniform", "Uniform"),
    KARRAS("karras", "Karras"),
    EXPONENTIAL("exponential", "Exponential"),
    POLYEXPONENTIAL("polyexponential", "Polyexponential"),
    SGM_UNIFORM("sgm_uniform", "SGM Uniform"),
    KL_OPTIMAL("kl_optimal", "KL Optimal"),
    ALIGN_YOUR_STEPS("align_your_steps", "Align Your Steps"),
    SIMPLE("simple", "Simple"),
    NORMAL("normal", "Normal"),
    DDIM("ddim_uniform", "DDIM Uniform"),
    BETA("beta", "Beta");

    companion object {
        /**
         * Converts SDAI data with `fromAlias`.
         *
         * @param alias alias value consumed by the API.
         * @return Result produced by `fromAlias`.
         * @author Dmitriy Moroz
         */
        fun fromAlias(alias: String): Scheduler =
            entries.find { it.alias.equals(alias, ignoreCase = true) } ?: AUTOMATIC
    }
}
