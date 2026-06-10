package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Defines the `BuildInfoProvider` contract for the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
interface BuildInfoProvider {
    /**
     * Exposes the `isDebug` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val isDebug: Boolean
    /**
     * Exposes the `buildNumber` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val buildNumber: Int
    /**
     * Exposes the `version` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val version: BuildVersion
    /**
     * Exposes the `type` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    val type: BuildType

    /**
     * Provides the `companion object` singleton used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `stub` value used by the SDAI core common layer.
         *
         * @author Dmitriy Moroz
         */
        val stub = object : BuildInfoProvider {
            override val isDebug: Boolean = true
            override val buildNumber: Int = 0
            override val version: BuildVersion = BuildVersion()
            override val type: BuildType = BuildType.FOSS

            override fun toString(): String = displayString()
        }
    }
}
