package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.presentation.R

@Composable
fun ForceUpdateDialog(
    openMarket: () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        onDismissRequest = EmptyLambda,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
        confirmButton = {
            TextButton(onClick = openMarket) {
                Text(text = stringResource(id = R.string.action_update))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.update_required_title),
                fontSize = 18.sp,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.update_required_sub_title),
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )
        }
    )
}

@Composable
@Preview
private fun ForceUpdateDialogPreview() {
    ForceUpdateDialog()
}
