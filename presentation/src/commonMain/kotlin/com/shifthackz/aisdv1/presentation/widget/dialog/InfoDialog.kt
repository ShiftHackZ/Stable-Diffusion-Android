package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.runtime.Composable
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

@Composable
fun InfoDialog(
    title: UiText,
    subTitle: UiText,
    onDismissRequest: () -> Unit = {},
) {
    InfoDialogContent(
        title = title.asString(),
        subTitle = subTitle.asString(),
        okText = Localization.string("ok"),
        onDismissRequest = onDismissRequest,
    )
}
