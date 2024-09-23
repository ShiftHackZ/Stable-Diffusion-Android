package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.core.localization.formatter.DurationFormatter
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ProgressDialog(
    @StringRes titleResId: Int = LocalizationR.string.communicating_progress_title,
    @StringRes subTitleResId: Int = LocalizationR.string.communicating_progress_sub_title,
    canDismiss: Boolean = true,
    onDismissRequest: () -> Unit = {},
    waitTimeSeconds: Int? = null,
    positionInQueue: Int? = null,
    step: Pair<Int, Int>? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = canDismiss,
            dismissOnBackPress = canDismiss,
        ),
    ) {
        GeneratingProgressDialogContent(
            titleResId = titleResId,
            subTitleResId = subTitleResId,
            waitTimeSeconds = waitTimeSeconds,
            positionInQueue = positionInQueue,
            step = step,
            content = content,
        )
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
                stringResource(
                    LocalizationR.string.communicating_wait_time,
                    DurationFormatter.formatDurationInSeconds(seconds)
                )
            } ?: "",
            style = TextStyle(fontSize = 12.sp),
            color = AlertDialogDefaults.textContentColor,
        )
        Text(
            text = positionInQueue?.let { position ->
                stringResource(id = LocalizationR.string.communicating_status_queue, position)
            } ?: step?.let { (current, total) ->
                stringResource(id = LocalizationR.string.communicating_status_steps, current, total)
            } ?: "",
            style = TextStyle(fontSize = 12.sp),
            color = AlertDialogDefaults.textContentColor,
        )
    }
}

@Composable
fun ProgressDialogCancelButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        OutlinedButton(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(0.7f),
            onClick = onClick,
        ) {
            Text(
                text = stringResource(id = LocalizationR.string.cancel),
                color = LocalContentColor.current,
            )
        }
    }
}

@Composable
fun GeneratingProgressDialogContent(
    @StringRes titleResId: Int = LocalizationR.string.communicating_progress_title,
    @StringRes subTitleResId: Int = LocalizationR.string.communicating_progress_sub_title,
    waitTimeSeconds: Int? = null,
    positionInQueue: Int? = null,
    step: Pair<Int, Int>? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
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
            content?.invoke()
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CommunicationProgressDialogPreview() {
    ProgressDialog()
}
