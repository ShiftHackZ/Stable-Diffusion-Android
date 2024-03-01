package com.shifthackz.aisdv1.presentation.screen.setup.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BaseServerSetupStateWrapper(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        content()
        Spacer(modifier = Modifier.height(32.dp))
    }
}
