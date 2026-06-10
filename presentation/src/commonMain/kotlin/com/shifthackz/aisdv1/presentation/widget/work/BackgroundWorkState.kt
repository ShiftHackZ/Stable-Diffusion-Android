package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.mvi.MviState

data class BackgroundWorkState(
    val visible: Boolean = false,
    val title: String = "",
    val subTitle: String = "",
    val image: ImageBitmap? = null,
    val isError: Boolean = false,
) : MviState
