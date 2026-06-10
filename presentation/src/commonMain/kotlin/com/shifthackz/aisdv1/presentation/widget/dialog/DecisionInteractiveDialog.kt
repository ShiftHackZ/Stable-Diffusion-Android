package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText

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
