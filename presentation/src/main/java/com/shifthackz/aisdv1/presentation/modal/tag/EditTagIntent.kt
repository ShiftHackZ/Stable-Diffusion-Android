package com.shifthackz.aisdv1.presentation.modal.tag

import com.shifthackz.android.core.mvi.MviIntent

sealed interface EditTagIntent : MviIntent {

    data object Close : EditTagIntent

    data class InitialData(
        val prompt: String,
        val negativePrompt: String,
        val tag: String,
        val isNegative: Boolean,
    ) : EditTagIntent

    data class UpdateTag(val tag: String) : EditTagIntent

    data class UpdateValue(val value: Double) : EditTagIntent

    enum class Value : EditTagIntent {
        Increment, Decrement;
    }

    enum class Action : EditTagIntent {
        Apply, Delete;
    }
}
