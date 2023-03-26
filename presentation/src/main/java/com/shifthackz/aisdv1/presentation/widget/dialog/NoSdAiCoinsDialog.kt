package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.presentation.R

@Composable
fun NoSdAiCoinsDialog(onDismissRequest: () -> Unit = {}) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.ok))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.sdai_coins_zero_title),
                fontSize = 18.sp,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.sdai_coins_zero_sub_title),
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )

        }
    )
}
