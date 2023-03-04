package com.shifthackz.aisdv1.presentation.screen.txt2img.contract

import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.presentation.screen.txt2img.model.StableDiffusionModelUi

sealed interface TextToImageState : MviState {
    object Idle : TextToImageState
    data class Content(val models: List<StableDiffusionModelUi>): TextToImageState
    data class Image(val image: String) : TextToImageState
}
