package com.shifthackz.aisdv1.presentation.screen.setup.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays the blocking loading state while setup data is being resolved.
 *
 * @param text localized status message shown under the spinner.
 * @param modifier outer layout modifier supplied by the host screen.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ServerSetupLoading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(56.dp))
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
