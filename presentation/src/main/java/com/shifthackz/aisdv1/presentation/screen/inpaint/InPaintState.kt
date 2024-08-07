package com.shifthackz.aisdv1.presentation.screen.inpaint

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.presentation.model.InPaintModel
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviState
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.R as PresentationR

@Immutable
data class InPaintState(
    val screenModal: Modal = Modal.None,
    val bitmap: Bitmap? = null,
    val selectedTab: Tab = Tab.IMAGE,
    val size: Int = 16,
    val model: InPaintModel = InPaintModel(),
) : MviState {

    enum class Tab(
        @StringRes val label: Int,
        @DrawableRes val iconRes: Int,
    ) {
        IMAGE(
            LocalizationR.string.in_paint_tab_1,
            PresentationR.drawable.ic_image,
        ),
        FORM(
            LocalizationR.string.in_paint_tab_2,
            PresentationR.drawable.ic_image,
        );
    }
}
