package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.utils.formatDuration

@Composable
fun ProgressDialog(
    @StringRes titleResId: Int = R.string.communicating_progress_title,
    @StringRes subTitleResId: Int = R.string.communicating_progress_sub_title,
    canDismiss: Boolean = true,
    onDismissRequest: () -> Unit = {},
    waitTimeSeconds: Int? = null,
    positionInQueue: Int? = null,
    step: Pair<Int, Int>? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = canDismiss,
            dismissOnBackPress = canDismiss,
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = AlertDialogDefaults.containerColor,
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text(
                    text = stringResource(id = titleResId),
                    style = TextStyle(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    color = AlertDialogDefaults.titleContentColor,
                )
                Text(
                    modifier = Modifier.padding(top = 14.dp),
                    text = stringResource(id = subTitleResId),
                    style = TextStyle(fontSize = 14.sp),
                    color = AlertDialogDefaults.textContentColor,
                )
                ProgressDialogStatus(
                    waitTimeSeconds = waitTimeSeconds,
                    positionInQueue = positionInQueue,
                    step = step,
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = AlertDialogDefaults.iconContentColor,
                )
            }
        }
    }
}

@Composable
fun ProgressDialogStatus(
    modifier: Modifier = Modifier,
    waitTimeSeconds: Int?,
    positionInQueue: Int?,
    step: Pair<Int, Int>?,
) {
    Column(modifier.padding(vertical = 4.dp)) {
        Text(
            text = waitTimeSeconds?.let { seconds ->
                stringResource(id = R.string.communicating_wait_time, formatDuration(seconds))
            } ?: "",
            style = TextStyle(fontSize = 12.sp),
            color = AlertDialogDefaults.textContentColor,
        )
        Text(
            text = positionInQueue?.let { position ->
                stringResource(id = R.string.communicating_status_queue, position)
            } ?: step?.let { (current, total) ->
                stringResource(id = R.string.communicating_status_steps, current, total)
            } ?: "",
            style = TextStyle(fontSize = 12.sp),
            color = AlertDialogDefaults.textContentColor,
        )

    }
}

@Composable
@Preview(showBackground = true)
private fun CommunicationProgressDialogPreview() {
    ProgressDialog()
}
