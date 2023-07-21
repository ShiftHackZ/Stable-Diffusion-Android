package com.shifthackz.aisdv1.presentation.screen.home

import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.utils.Constants

data class HomeNavigationState(
    val bottomAdBanner: Boolean = false,
) : MviState

fun AiGenerationResult.Type.mapToRoute(): String = when (this) {
    AiGenerationResult.Type.TEXT_TO_IMAGE -> Constants.ROUTE_TXT_TO_IMG
    AiGenerationResult.Type.IMAGE_TO_IMAGE -> Constants.ROUTE_IMG_TO_IMG
}
