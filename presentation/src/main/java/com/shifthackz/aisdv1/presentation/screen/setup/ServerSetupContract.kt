package com.shifthackz.aisdv1.presentation.screen.setup

import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviEffect
import com.shifthackz.aisdv1.core.ui.MviState
import com.shifthackz.aisdv1.core.validation.ValidationResult
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.presentation.R

sealed interface ServerSetupEffect : MviEffect {
    object CompleteSetup : ServerSetupEffect
}

data class ServerSetupState(
    val showBackNavArrow: Boolean = false,
    val screenDialog: Dialog = Dialog.None,
    val serverUrl: String = "",
    val originalSeverUrl: String = "",
    val demoMode: Boolean = false,
    val originalDemoMode: Boolean = false,
    val validationError: UiText? = null,
) : MviState {

    sealed interface Dialog {
        object None : Dialog
        object Communicating : Dialog
        data class Error(val error: UiText) : Dialog
    }
}

enum class ServerSetupLaunchSource(val key: Int) {
    SPLASH(0),
    SETTINGS(1);

    companion object {
        fun fromKey(key: Int) = values().firstOrNull { it.key == key } ?: SPLASH
    }
}

fun ValidationResult<UrlValidator.Error>.mapToUi(): UiText? {
    if (this.isValid) return null
    return when (validationError as UrlValidator.Error) {
        UrlValidator.Error.BadScheme -> R.string.error_invalid_scheme
        UrlValidator.Error.Empty -> R.string.error_empty_url
        UrlValidator.Error.Invalid -> R.string.error_invalid_url
    }.asUiText()
}
