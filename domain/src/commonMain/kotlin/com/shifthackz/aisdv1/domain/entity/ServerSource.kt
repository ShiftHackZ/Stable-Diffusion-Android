package com.shifthackz.aisdv1.domain.entity

import com.shifthackz.aisdv1.core.common.appbuild.BuildType

/**
 * Coordinates `ServerSource` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
enum class ServerSource(
    /**
     * Exposes the `key` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val key: String,
    /**
     * Exposes the `featureTags` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val featureTags: Set<FeatureTag>,
    /**
     * Exposes the `allowedInBuilds` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val allowedInBuilds: Set<BuildType> = setOf(BuildType.FOSS, BuildType.PLAY, BuildType.FULL),
) {
    AUTOMATIC1111(
        key = "custom",
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
    HORDE(
        key = "horde",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.Batch,
        ),
    ),
    HUGGING_FACE(
        key = "hugging_face",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    ),
    OPEN_AI(
        key = "open_ai",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Batch,
        ),
    ),
    STABILITY_AI(
        key = "stability_ai",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.Batch,
        ),
    ),
    LOCAL_MICROSOFT_ONNX(
        key = "local",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
        ),
    ),
    LOCAL_GOOGLE_MEDIA_PIPE(
        key = "local_google_media_pipe",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
        ),
        allowedInBuilds = setOf(BuildType.PLAY, BuildType.FULL),
    ),
    LOCAL_APPLE_CORE_ML(
        key = "local_apple_core_ml",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
        ),
    );

    companion object {
        fun parse(value: String) = entries.find { it.key == value } ?: AUTOMATIC1111
    }
}
