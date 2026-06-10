package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

/**
 * Renders the `ErrorDialog` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param onDismissRequest callback invoked by the component.
 * @author Dmitriy Moroz
 */
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
