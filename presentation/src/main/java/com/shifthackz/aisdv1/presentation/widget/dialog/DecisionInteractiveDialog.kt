package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun DecisionInteractiveDialog(
    title: UiText,
    text: UiText,
    @StringRes confirmActionResId: Int = LocalizationR.string.ok,
    @StringRes dismissActionResId: Int = LocalizationR.string.cancel,
    onConfirmAction: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmAction) {
                Text(text = stringResource(id = confirmActionResId))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = dismissActionResId))
            }
        },
        title = {
            Text(
                text = title.asString(),
                fontSize = 18.sp,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Text(
                text = text.asString(),
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )
            content()
        }
    )
}

