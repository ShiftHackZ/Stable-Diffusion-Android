package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Coordinates `BuildType` behavior in the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
enum class BuildType {
    FULL,
    FOSS,
    PLAY;

    /**
     * Provides the `companion object` singleton used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Executes the `fromBuildConfig` step in the SDAI core common layer.
         *
         * @param input input value consumed by the API.
         * @author Dmitriy Moroz
         */
        fun fromBuildConfig(input: String) = when (input) {
            "FULL" -> FULL
            "FOSS" -> FOSS
            else -> PLAY
        }
    }
}
