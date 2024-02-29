package com.shifthackz.aisdv1.domain.entity

data class HuggingFaceModel(
    val id: String,
    val name: String,
    val alias: String,
    val source: String,
) {
    companion object {
        val default = HuggingFaceModel(
            id = "abed08ad-e1d3-440b-b433-476aad528006",
            name = "Open Journey",
            alias = "prompthero/openjourney-v4",
            source = "https://huggingface.co/prompthero/openjourney-v4",
        )
    }
}
