package com.shifthackz.aisdv1.core.common.appbuild

class BuildVersion : Comparable<BuildVersion> {
    private var major: Int = 0
    private var minor: Int = 0
    private var patch: Int = 0
    private var tag: String = ""

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

    override fun compareTo(other: BuildVersion): Int {
        if (this.major > other.major) return 1
        if (this.major < other.major) return -1
        if (this.minor > other.minor) return 1
        if (this.minor < other.minor) return -1
        if (this.patch > other.patch) return 1
        if (this.patch < other.patch) return -1
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildVersion

        if (major != other.major) return false
        if (minor != other.minor) return false
        if (patch != other.patch) return false

        return true
    }

    override fun hashCode(): Int {
        var result = major
        result = 31 * result + minor
        result = 31 * result + patch
        return result
    }

    override fun toString(): String = buildString {
        append("$major.$minor.$patch")
        tag.takeIf(String::isNotEmpty)?.let { append("-$tag") }
    }

    companion object {
        const val DELIMITER_TAG = "-"
        const val DELIMITER_VERSION = "."
    }
}
