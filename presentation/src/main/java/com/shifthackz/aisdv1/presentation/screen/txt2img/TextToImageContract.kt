package com.shifthackz.aisdv1.presentation.screen.txt2img

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.entity.TextToImagePayloadDomain

sealed interface TextToImageEffect : MviEffect

sealed interface TextToImageState : MviState {
    val screenDialog: Dialog

    object Uninitialized : TextToImageState {
        override val screenDialog = Dialog.None
    }

    data class Content(
        override val screenDialog: Dialog = Dialog.None,
        val models: List<String>,
        val selectedModel: UiText = UiText.empty,
        val prompt: String = "",
        val negativePrompt: String = "",
        val samplingSteps: Int = 20,
    ) : TextToImageState

    sealed interface Dialog {
        object None : Dialog

    }
}

fun TextToImageState.Content.mapToPayload(): TextToImagePayloadDomain = with(this) {
    TextToImagePayloadDomain(
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        width = 512,
        height = 512,
        restoreFaces = true
    )
}
