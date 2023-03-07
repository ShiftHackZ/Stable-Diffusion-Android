package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState

sealed interface ImageToImageEffect : MviEffect

data class ImageToImageState(
    private val cc: String? = null
) : MviState

