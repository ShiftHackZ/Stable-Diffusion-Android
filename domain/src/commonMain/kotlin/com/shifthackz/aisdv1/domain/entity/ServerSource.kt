package com.shifthackz.aisdv1.domain.entity

import com.shifthackz.aisdv1.core.common.appbuild.BuildType

/**
 * Provider catalog entry used by setup, onboarding, and settings screens.
 *
 * @property key persisted configuration id; changing it migrates user-selected provider values.
 * @property type broad hosting model used by provider filters.
 * @property readiness user-facing stability marker for the integration.
 * @property version last meaningful implementation update in `yyyy.M.d` format, optionally suffixed with a tag.
 * @property featureTags searchable capability labels shown in provider selection UI.
 * @property allowedInBuilds app flavors where the provider can be selected.
 */
enum class ServerSource(
    val key: String,
    val type: ServerSourceType,
    val readiness: ServerSourceReadiness,
    val version: String,
    val featureTags: Set<FeatureTag>,
    val allowedInBuilds: Set<BuildType> = setOf(BuildType.FOSS, BuildType.PLAY, BuildType.FULL),
) {
    AUTOMATIC1111(
        key = "custom",
        type = ServerSourceType.SELF_HOSTED,
        readiness = ServerSourceReadiness.STABLE,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.OwnServer,
            FeatureTag.MultipleModels,
            FeatureTag.Lora,
            FeatureTag.TextualInversion,
            FeatureTag.HyperNetworks,
            FeatureTag.Batch,
        ),
    ),
    SWARM_UI(
        key = "swarm_ui",
        type = ServerSourceType.SELF_HOSTED,
        readiness = ServerSourceReadiness.STABLE,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.OwnServer,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Lora,
            FeatureTag.TextualInversion,
            FeatureTag.Batch,
        ),
    ),
    LOCAL_MICROSOFT_ONNX(
        key = "local",
        type = ServerSourceType.LOCAL,
        readiness = ServerSourceReadiness.BETA,
        version = "2024.9.23",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
        ),
    ),
    LOCAL_GOOGLE_MEDIA_PIPE(
        key = "local_google_media_pipe",
        type = ServerSourceType.LOCAL,
        readiness = ServerSourceReadiness.BETA,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
        ),
        allowedInBuilds = setOf(BuildType.PLAY, BuildType.FULL),
    ),
    LOCAL_STABLE_DIFFUSION_CPP(
        key = "local_stable_diffusion_cpp",
        type = ServerSourceType.LOCAL,
        readiness = ServerSourceReadiness.ALPHA,
        version = "2026.6.13",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
        ),
    ),
    LOCAL_APPLE_CORE_ML(
        key = "local_apple_core_ml",
        type = ServerSourceType.LOCAL,
        readiness = ServerSourceReadiness.ALPHA,
        version = "2026.6.12",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    ),
    HORDE(
        key = "horde",
        type = ServerSourceType.CLOUD,
        readiness = ServerSourceReadiness.STABLE,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.Batch,
        ),
    ),
    HUGGING_FACE(
        key = "hugging_face",
        type = ServerSourceType.CLOUD,
        readiness = ServerSourceReadiness.STABLE,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    ),
    OPEN_AI(
        key = "open_ai",
        type = ServerSourceType.CLOUD,
        readiness = ServerSourceReadiness.STABLE,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    ),
    STABILITY_AI(
        key = "stability_ai",
        type = ServerSourceType.CLOUD,
        readiness = ServerSourceReadiness.STABLE,
        version = "2026.6.10",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    ),
    FAL_AI(
        key = "fal_ai",
        type = ServerSourceType.CLOUD,
        readiness = ServerSourceReadiness.ALPHA,
        version = "2026.6.11",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    );

    companion object {
        fun parse(value: String) = entries.find { it.key == value } ?: AUTOMATIC1111
    }
}

/**
 * Broad hosting model used by provider filters and onboarding copy.
 */
enum class ServerSourceType {
    SELF_HOSTED,
    CLOUD,
    LOCAL,
}

/**
 * User-facing stability level for a provider integration.
 */
enum class ServerSourceReadiness {
    EXPERIMENTAL,
    ALPHA,
    BETA,
    STABLE,
}
