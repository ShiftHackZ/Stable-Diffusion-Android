package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.ui.graphics.ImageBitmap
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `BackgroundWorkState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class BackgroundWorkState(
    /**
     * Exposes the `visible` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val visible: Boolean = false,
    /**
     * Exposes the `title` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String = "",
    /**
     * Exposes the `subTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val subTitle: String = "",
    /**
     * Exposes the `image` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val image: ImageBitmap? = null,
    /**
     * Exposes the `isError` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val isError: Boolean = false,
) : MviState
