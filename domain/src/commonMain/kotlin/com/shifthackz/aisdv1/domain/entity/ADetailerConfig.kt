package com.shifthackz.aisdv1.domain.entity

/**
 * Carries ADetailer extension configuration through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class ADetailerConfig(
    /**
     * Exposes the `enabled` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val enabled: Boolean = false,
    /**
     * Exposes the `model` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val model: String = "face_yolov8s.pt",
    /**
     * Exposes the `prompt` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val prompt: String = "",
    /**
     * Exposes the `negativePrompt` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val negativePrompt: String = "",
    /**
     * Exposes the `confidence` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val confidence: Float = 0.3f,
    /**
     * Exposes the `maskBlur` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val maskBlur: Int = 4,
    /**
     * Exposes the `denoisingStrength` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val denoisingStrength: Float = 0.4f,
    /**
     * Exposes the `inpaintOnlyMasked` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val inpaintOnlyMasked: Boolean = true,
    /**
     * Exposes the `inpaintPadding` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val inpaintPadding: Int = 32,
) {
    companion object {
        /**
         * Exposes the `DISABLED` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val DISABLED = ADetailerConfig(enabled = false)
        /**
         * Exposes the `AVAILABLE_MODELS` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val AVAILABLE_MODELS = listOf(
            "face_yolov8n.pt",
            "face_yolov8s.pt",
            "hand_yolov8n.pt",
            "person_yolov8n-seg.pt",
            "person_yolov8s-seg.pt",
            "yolov8x-worldv2.pt",
            "mediapipe_face_full",
            "mediapipe_face_short",
            "mediapipe_face_mesh",
            "mediapipe_face_mesh_eyes_only",
        )
    }
}
