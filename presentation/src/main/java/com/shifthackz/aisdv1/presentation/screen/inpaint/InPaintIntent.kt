package com.shifthackz.aisdv1.presentation.screen.inpaint

import androidx.compose.ui.graphics.Path
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.android.core.mvi.MviIntent

sealed interface InPaintIntent : MviIntent {

    data object NavigateBack : InPaintIntent

    data class SelectTab(val tab: InPaintState.Tab) : InPaintIntent

    data class ChangeCapSize(val size: Int) : InPaintIntent

    data class Draw(val path: Path) : InPaintIntent

    enum class Action : InPaintIntent {
        Undo, Clear;
    }

    sealed interface ScreenModal : InPaintIntent {

        data class Show(val modal: Modal) : ScreenModal

        data object Dismiss : ScreenModal
    }
}
