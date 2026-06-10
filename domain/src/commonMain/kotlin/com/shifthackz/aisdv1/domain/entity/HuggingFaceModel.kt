package com.shifthackz.aisdv1.domain.entity

/**
 * Carries `HuggingFaceModel` data through the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
data class HuggingFaceModel(
    /**
     * Exposes the `id` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val id: String,
    /**
     * Exposes the `name` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String,
    /**
     * Exposes the `alias` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val alias: String,
    /**
     * Exposes the `source` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val source: String,
) {
    /**
     * Provides the `companion object` singleton used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `fluxSchnell` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        private val fluxSchnell = HuggingFaceModel(
            id = "black-forest-labs/FLUX.1-schnell",
            name = "FLUX.1 Schnell",
            alias = "black-forest-labs/FLUX.1-schnell",
            source = "https://huggingface.co/black-forest-labs/FLUX.1-schnell",
        )

        /**
         * Exposes the `stableDiffusion3Medium` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        private val stableDiffusion3Medium = HuggingFaceModel(
            id = "stabilityai/stable-diffusion-3-medium-diffusers",
            name = "Stable Diffusion 3 Medium",
            alias = "stabilityai/stable-diffusion-3-medium-diffusers",
            source = "https://huggingface.co/stabilityai/stable-diffusion-3-medium-diffusers",
        )

        /**
         * Exposes the `supportedHfInferenceTextToImageModels` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val supportedHfInferenceTextToImageModels = listOf(
            fluxSchnell,
            stableDiffusion3Medium,
        )

        /**
         * Exposes the `supportedHfInferenceTextToImageAliases` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val supportedHfInferenceTextToImageAliases =
            supportedHfInferenceTextToImageModels.map(HuggingFaceModel::alias).toSet()

        /**
         * Exposes the `default` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        val default = HuggingFaceModel(
            id = fluxSchnell.id,
            name = fluxSchnell.name,
            alias = fluxSchnell.alias,
            source = fluxSchnell.source,
        )
    }
}
