package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.localization.formatter.DurationFormatter
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText

@Composable
fun ProgressDialog(
    title: UiText = Localization.string("communicating_progress_title").asUiText(),
    subTitle: UiText = Localization.string("communicating_progress_sub_title").asUiText(),
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
            title = title,
            subTitle = subTitle,
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
    ProgressDialogStatusContent(
        modifier = modifier,
        waitTimeText = waitTimeText(waitTimeSeconds),
        statusText = statusText(positionInQueue, step),
    )
}

@Composable
fun ProgressDialogCancelButton(onClick: () -> Unit) {
    ProgressDialogCancelButtonContent(
        cancelText = Localization.string("cancel"),
        onClick = onClick,
    )
}

@Composable
fun GeneratingProgressDialogContent(
    title: UiText = Localization.string("communicating_progress_title").asUiText(),
    subTitle: UiText = Localization.string("communicating_progress_sub_title").asUiText(),
    waitTimeSeconds: Int? = null,
    positionInQueue: Int? = null,
    step: Pair<Int, Int>? = null,
    content: (@Composable () -> Unit)? = null,
) {
    ProgressDialogContent(
        title = title.asString(),
        subTitle = subTitle.asString(),
        waitTimeText = waitTimeText(waitTimeSeconds),
        statusText = statusText(positionInQueue, step),
        content = content,
    )
}

private fun waitTimeText(waitTimeSeconds: Int?): String? {
    return waitTimeSeconds?.let { seconds ->
        Localization.string(
            "communicating_wait_time",
            DurationFormatter.formatDurationInSeconds(seconds),
        )
    }
}

private fun statusText(
    positionInQueue: Int?,
    step: Pair<Int, Int>?,
): String? {
    return positionInQueue?.let { position ->
        Localization.string("communicating_status_queue", position)
    } ?: step?.let { (current, total) ->
        Localization.string("communicating_status_steps", current, total)
    }
}
