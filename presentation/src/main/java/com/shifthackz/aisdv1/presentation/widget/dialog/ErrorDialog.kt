package com.shifthackz.aisdv1.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.presentation.R

@Composable
fun ErrorDialog(
    text: UiText,
    onDismissRequest: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color.White,
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)) {
                Text(
                    text = stringResource(id = R.string.error_title),
                    style = TextStyle(fontSize = 16.sp, /*color = mineShaft*/),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.padding(top = 32.dp),
                    text = text.asString(),
                    style = TextStyle(fontSize = 14.sp, /*color = pureBlackLight*/),
                )

                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        }
    }
}
