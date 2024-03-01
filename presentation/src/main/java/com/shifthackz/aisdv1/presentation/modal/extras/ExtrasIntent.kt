package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.android.core.mvi.MviIntent

sealed interface ExtrasIntent : MviIntent {

    data class ToggleItem(val item: ExtraItemUi) : ExtrasIntent

    data object ApplyPrompts : ExtrasIntent

    data object Close : ExtrasIntent
}
