package com.shifthackz.aisdv1.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.presentation.R

@Composable
fun CommunicationProgressDialog(
    canDismiss: Boolean = true,
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnClickOutside = canDismiss,
            dismissOnBackPress = canDismiss,
        ),
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)) {
                Text(
                    text = stringResource(id = R.string.communicating_progress_title),
                    style = TextStyle(fontSize = 16.sp, /*color = mineShaft*/),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = stringResource(id = R.string.communicating_progress_sub_title),
                    style = TextStyle(fontSize = 14.sp, /*color = pureBlackLight*/),
                )

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = Color.Black,
                    //backgroundColor = Color.Magenta,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CommunicationProgressDialogPreview() {
    CommunicationProgressDialog()
}
