package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText

/**
 * Renders the `DecisionInteractiveDialog` UI for the SDAI presentation layer.
 *
 * @param title title value consumed by the API.
 * @param text text value consumed by the API.
 * @param confirmActionText confirm action text value consumed by the API.
 * @param dismissActionText dismiss action text value consumed by the API.
 * @param onConfirmAction callback invoked by the component.
 * @param onDismissRequest callback invoked by the component.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun DecisionInteractiveDialog(
    title: UiText,
    text: UiText,
    confirmActionText: UiText = Localization.string("ok").asUiText(),
    dismissActionText: UiText = Localization.string("cancel").asUiText(),
    onConfirmAction: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    DecisionInteractiveDialogContent(
        title = title.asString(),
        text = text.asString(),
        confirmActionText = confirmActionText.asString(),
        dismissActionText = dismissActionText.asString(),
        onConfirmAction = onConfirmAction,
        onDismissRequest = onDismissRequest,
        content = content,
    )
}
