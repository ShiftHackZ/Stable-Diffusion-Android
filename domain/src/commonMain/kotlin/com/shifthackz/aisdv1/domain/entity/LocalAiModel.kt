package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `LocalAiModel` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class LocalAiModel(
    /**
     * Exposes the `id` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val id: String,
    /**
     * Exposes the `type` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val type: Type,
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `size` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val size: String,
    /**
     * Exposes the `sources` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val sources: List<String>,
    /**
     * Exposes the `downloaded` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val downloaded: Boolean = false,
    /**
     * Exposes the `selected` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val selected: Boolean = false,
) {
    enum class Type(val key: String) {
        ONNX("onnx"),
        MediaPipe("mediapipe"),
        Sdxl("sdxl"),
        CoreMl("coreml"),
        Bonsai("bonsai");

        companion object {
            fun parse(value: String?) = entries.find { it.key == value } ?: ONNX
        }
    }

    companion object {
        val CustomOnnx = LocalAiModel(
            id = "CUSTOM",
            type = Type.ONNX,
            name = "Custom",
            size = "NaN",
            sources = emptyList(),
        )

        val CustomMediaPipe = LocalAiModel(
            id = "CUSTOM_MP",
            type = Type.MediaPipe,
            name = "Custom",
            size = "NaN",
            sources = emptyList(),
        )

        val CustomSdxl = LocalAiModel(
            id = "CUSTOM_SDXL",
            type = Type.Sdxl,
            name = "Custom",
            size = "NaN",
            sources = emptyList(),
        )

        val CustomCoreMl = LocalAiModel(
            id = "CUSTOM_CORE_ML",
            type = Type.CoreMl,
            name = "Custom",
            size = "NaN",
            sources = emptyList(),
        )

        val CustomBonsai = LocalAiModel(
            id = "CUSTOM_BONSAI",
            type = Type.Bonsai,
            name = "Custom",
            size = "NaN",
            sources = emptyList(),
        )
    }
}
