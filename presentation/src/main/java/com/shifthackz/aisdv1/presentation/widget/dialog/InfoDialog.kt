package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.localization.R
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString

@Composable
fun InfoDialog(
    title: UiText,
    subTitle: UiText,
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.ok))
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
                text = subTitle.asString(),
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )
        }
    )
}
