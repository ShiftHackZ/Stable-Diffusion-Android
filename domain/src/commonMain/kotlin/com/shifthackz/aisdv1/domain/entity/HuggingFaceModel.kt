package com.shifthackz.aisdv1.domain.entity

data class HuggingFaceModel(
    val id: String,
    val name: String,
    val alias: String,
    val source: String,
) {
    companion object {
        private val fluxSchnell = HuggingFaceModel(
            id = "black-forest-labs/FLUX.1-schnell",
            name = "FLUX.1 Schnell",
            alias = "black-forest-labs/FLUX.1-schnell",
            source = "https://huggingface.co/black-forest-labs/FLUX.1-schnell",
        )

        private val stableDiffusion3Medium = HuggingFaceModel(
            id = "stabilityai/stable-diffusion-3-medium-diffusers",
            name = "Stable Diffusion 3 Medium",
            alias = "stabilityai/stable-diffusion-3-medium-diffusers",
            source = "https://huggingface.co/stabilityai/stable-diffusion-3-medium-diffusers",
        )

        val supportedHfInferenceTextToImageModels = listOf(
            fluxSchnell,
            stableDiffusion3Medium,
        )

        val supportedHfInferenceTextToImageAliases =
            supportedHfInferenceTextToImageModels.map(HuggingFaceModel::alias).toSet()

        val default = HuggingFaceModel(
            id = fluxSchnell.id,
            name = fluxSchnell.name,
            alias = fluxSchnell.alias,
            source = fluxSchnell.source,
        )
    }
}
