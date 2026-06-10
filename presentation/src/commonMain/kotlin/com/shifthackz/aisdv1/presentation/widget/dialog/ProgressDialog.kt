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

/**
 * Renders the `ProgressDialog` UI for the SDAI presentation layer.
 *
 * @param title title value consumed by the API.
 * @param subTitle sub title value consumed by the API.
 * @param canDismiss can dismiss value consumed by the API.
 * @param onDismissRequest callback invoked by the component.
 * @param waitTimeSeconds wait time seconds value consumed by the API.
 * @param positionInQueue position in queue value consumed by the API.
 * @param step step value consumed by the API.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `ProgressDialogStatus` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param waitTimeSeconds wait time seconds value consumed by the API.
 * @param positionInQueue position in queue value consumed by the API.
 * @param step step value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `ProgressDialogCancelButton` UI for the SDAI presentation layer.
 *
 * @param onClick callback invoked when the user activates the control.
 * @author Dmitriy Moroz
 */
@Composable
fun ProgressDialogCancelButton(onClick: () -> Unit) {
    ProgressDialogCancelButtonContent(
        cancelText = Localization.string("cancel"),
        onClick = onClick,
    )
}

/**
 * Renders the `GeneratingProgressDialogContent` UI for the SDAI presentation layer.
 *
 * @param title title value consumed by the API.
 * @param subTitle sub title value consumed by the API.
 * @param waitTimeSeconds wait time seconds value consumed by the API.
 * @param positionInQueue position in queue value consumed by the API.
 * @param step step value consumed by the API.
 * @param content content value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `waitTimeText` step in the SDAI presentation layer.
 *
 * @param waitTimeSeconds wait time seconds value consumed by the API.
 * @return Result produced by `waitTimeText`.
 * @author Dmitriy Moroz
 */
private fun waitTimeText(waitTimeSeconds: Int?): String? {
    return waitTimeSeconds?.let { seconds ->
        Localization.string(
            "communicating_wait_time",
            DurationFormatter.formatDurationInSeconds(seconds),
        )
    }
}

/**
 * Executes the `statusText` step in the SDAI presentation layer.
 *
 * @param positionInQueue position in queue value consumed by the API.
 * @param step step value consumed by the API.
 * @return Result produced by `statusText`.
 * @author Dmitriy Moroz
 */
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
