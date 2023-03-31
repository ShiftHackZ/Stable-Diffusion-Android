package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.presentation.R

@Composable
fun NoSdAiCoinsDialog(
    onDismissRequest: () -> Unit = {},
    launchRewarded: () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(
                onClick = {
                    onDismissRequest()
                    launchRewarded()
                },
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "free_coins_1",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "free_coins_2",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 10.dp),
                    text = stringResource(id = R.string.sdai_coins_free),
                )
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismissRequest) {
                Text(text = stringResource(id = R.string.action_close))
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

@Composable
@Preview
private fun NoSdAiCoinsDialogPreview() {
    NoSdAiCoinsDialog()
}
