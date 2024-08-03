package com.shifthackz.aisdv1.presentation.mocks

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

val mockHuggingFaceModels = listOf(
    HuggingFaceModel.default,
    HuggingFaceModel(
        "80974f2d-7ee0-48e5-97bc-448de3c1d634",
        "Analog Diffusion",
        "wavymulder/Analog-Diffusion",
        "https://huggingface.co/wavymulder/Analog-Diffusion",
    ),
)
