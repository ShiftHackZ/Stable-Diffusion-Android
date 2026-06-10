package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

@Composable
fun ErrorDialog(
    text: UiText,
    onDismissRequest: () -> Unit = {},
) {
    ErrorDialogContent(
        title = Localization.string("error_title"),
        text = text.asString(),
        okText = Localization.string("ok"),
        onDismissRequest = onDismissRequest,
    )
}
