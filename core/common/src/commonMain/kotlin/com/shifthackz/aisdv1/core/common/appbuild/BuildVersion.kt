package com.shifthackz.aisdv1.core.common.appbuild

/**
 * Coordinates `BuildVersion` behavior in the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
class BuildVersion : Comparable<BuildVersion> {
    /**
     * Exposes the `major` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    private var major: Int = 0
    /**
     * Exposes the `minor` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    private var minor: Int = 0
    /**
     * Exposes the `patch` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    private var patch: Int = 0
    /**
     * Exposes the `tag` value used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    private var tag: String = ""

    /**
     * Creates a new SDAI component instance.
     *
     * @param major major value consumed by the API.
     * @param minor minor value consumed by the API.
     * @param patch patch value consumed by the API.
     * @param tag tag value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(
        major: Int = 0,
        minor: Int = 0,
        patch: Int = 0,
        tag: String = "",
    ) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.tag = tag
    }

    /**
     * Creates a new SDAI component instance.
     *
     * @param versionString version string value consumed by the API.
     * @author Dmitriy Moroz
     */
    constructor(versionString: String?) {
        if (versionString.isNullOrBlank()) return
        val verWithTag = versionString.split(DELIMITER_TAG)
        verWithTag.getOrNull(1)?.let { this.tag = it }
        verWithTag.getOrNull(0)?.let { version ->
            val versionComponents = version.split(DELIMITER_VERSION)
            versionComponents.getOrNull(0)
                ?.toIntOrNull()
                ?.let { this.major = it }

            versionComponents.getOrNull(1)
                ?.toIntOrNull()
                ?.let { this.minor = it }

            versionComponents.getOrNull(2)
                ?.toIntOrNull()
                ?.let { this.patch = it }
        }
    }

    /**
     * Executes the `compareTo` step in the SDAI core common layer.
     *
     * @param other other value consumed by the API.
     * @return Result produced by `compareTo`.
     * @author Dmitriy Moroz
     */
    override fun compareTo(other: BuildVersion): Int {
        if (this.major > other.major) return 1
        if (this.major < other.major) return -1
        if (this.minor > other.minor) return 1
        if (this.minor < other.minor) return -1
        if (this.patch > other.patch) return 1
        if (this.patch < other.patch) return -1
        return 0
    }

    /**
     * Executes the `equals` step in the SDAI core common layer.
     *
     * @param other other value consumed by the API.
     * @return Result produced by `equals`.
     * @author Dmitriy Moroz
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BuildVersion) return false

        if (major != other.major) return false
        if (minor != other.minor) return false
        if (patch != other.patch) return false

        return true
    }

    /**
     * Executes the `hashCode` step in the SDAI core common layer.
     *
     * @return Result produced by `hashCode`.
     * @author Dmitriy Moroz
     */
    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        return result
    }

    /**
     * Converts SDAI data with `toString`.
     *
     * @return Result produced by `toString`.
     * @author Dmitriy Moroz
     */
    override fun toString(): String = buildString {
        append("$major.$minor.$patch")
        tag.takeIf(String::isNotEmpty)?.let { append("-$tag") }
    }

    /**
     * Provides the `companion object` singleton used by the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `DELIMITER_TAG` value used by the SDAI core common layer.
         *
         * @author Dmitriy Moroz
         */
        const val DELIMITER_TAG = "-"
        /**
         * Exposes the `DELIMITER_VERSION` value used by the SDAI core common layer.
         *
         * @author Dmitriy Moroz
         */
        const val DELIMITER_VERSION = "."
    }
}
