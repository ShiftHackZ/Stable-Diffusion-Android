package com.shifthackz.aisdv1.presentation.modal.extras

import com.shifthackz.aisdv1.core.ui.MviIntent

sealed interface ExtrasIntent : MviIntent {

    data class ToggleItem(val item: ExtraItemUi) : ExtrasIntent

    data object ApplyPrompts : ExtrasIntent

    data object Close : ExtrasIntent
}
