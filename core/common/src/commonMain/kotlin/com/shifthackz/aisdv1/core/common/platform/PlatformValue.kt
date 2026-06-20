package com.shifthackz.aisdv1.core.common.platform

/**
 * Small value holder for metadata that can differ between Android and iOS.
 *
 * It is used for provider readiness and similar catalog fields where a single
 * domain enum should stay stable while the user-facing platform status differs.
 */
data class PlatformValue<T>(
    val android: T,
    val ios: T,
) {
    constructor(value: T) : this(
        android = value,
        ios = value,
    )

    operator fun get(platform: Platform): T = when (platform) {
        Platform.ANDROID -> android
        Platform.IOS -> ios
    }
}
