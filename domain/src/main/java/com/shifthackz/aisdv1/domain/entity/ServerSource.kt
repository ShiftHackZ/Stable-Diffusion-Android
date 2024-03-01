package com.shifthackz.aisdv1.domain.entity

enum class ServerSource(
    val key: String,
    val featureTags: Set<FeatureTag>,
) {
    AUTOMATIC1111(
        key = "custom",
        featureTags = setOf(
            FeatureTag.Txt2Img,
            FeatureTag.Img2Img,
            FeatureTag.MultipleModels,
            FeatureTag.Lora,
            FeatureTag.TextualInversion,
            FeatureTag.HyperNetworks,
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
    LOCAL(
        key = "local",
        featureTags = setOf(
            FeatureTag.Offline,
            FeatureTag.Txt2Img,
            FeatureTag.MultipleModels,
        ),
    );

    companion object {
        fun parse(value: String) = entries.find { it.key == value } ?: AUTOMATIC1111
    }
}
