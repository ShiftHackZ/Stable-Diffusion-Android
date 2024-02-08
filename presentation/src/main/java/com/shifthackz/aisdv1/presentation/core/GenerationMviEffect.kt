package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.ui.MviEffect

sealed interface GenerationMviEffect : MviEffect {
    data class LaunchGalleryDetail(val itemId: Long) : GenerationMviEffect
}
