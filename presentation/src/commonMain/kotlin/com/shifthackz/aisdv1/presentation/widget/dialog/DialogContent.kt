package com.shifthackz.aisdv1.presentation.widget.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoDialogContent(
    title: String,
    subTitle: String,
    okText: String,
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = okText)
            }
        },
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Text(
                text = subTitle,
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )
        },
    )
}

@Composable
fun ErrorDialogContent(
    title: String,
    text: String,
    okText: String,
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = okText)
            }
        },
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Text(
                text = text,
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )
        },
    )
}

@Composable
fun DecisionInteractiveDialogContent(
    title: String,
    text: String,
    confirmActionText: String,
    dismissActionText: String,
    onConfirmAction: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    AlertDialog(
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.background,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirmAction) {
                Text(text = confirmActionText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = dismissActionText)
            }
        },
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                color = AlertDialogDefaults.titleContentColor,
            )
        },
        text = {
            Text(
                text = text,
                fontSize = 14.sp,
                color = AlertDialogDefaults.textContentColor,
            )
            content()
        },
    )
}

@Composable
fun ProgressDialogContent(
    title: String,
    subTitle: String,
    waitTimeText: String? = null,
    statusText: String? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Text(
                text = title,
                style = TextStyle(fontSize = 16.sp),
                fontWeight = FontWeight.Bold,
                color = AlertDialogDefaults.titleContentColor,
            )
            Text(
                modifier = Modifier.padding(top = 14.dp),
                text = subTitle,
                style = TextStyle(fontSize = 14.sp),
                color = AlertDialogDefaults.textContentColor,
            )
            ProgressDialogStatusContent(
                waitTimeText = waitTimeText,
                statusText = statusText,
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
fun ProgressDialogStatusContent(
    modifier: Modifier = Modifier,
    waitTimeText: String?,
    statusText: String?,
) {
    Column(modifier.padding(vertical = 4.dp)) {
        Text(
            text = waitTimeText.orEmpty(),
            style = TextStyle(fontSize = 12.sp),
            color = AlertDialogDefaults.textContentColor,
        )
        Text(
            text = statusText.orEmpty(),
            style = TextStyle(fontSize = 12.sp),
            color = AlertDialogDefaults.textContentColor,
        )
    }
}

@Composable
fun ProgressDialogCancelButtonContent(
    cancelText: String,
    onClick: () -> Unit,
) {
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
                text = cancelText,
                color = LocalContentColor.current,
            )
        }
    }
}
