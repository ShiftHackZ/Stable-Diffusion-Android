package com.shifthackz.aisdv1.presentation.screen.setup.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.components.ConfigurationModeButton

@Composable
fun SourceSelectionStep(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    handleIntent: (ServerSetupIntent) -> Unit = {},
) {
    BaseServerSetupStateWrapper(modifier) {
        Column {
            Spacer(modifier = Modifier.height(12.dp))
            state.allowedModes.forEach { mode ->
                ConfigurationModeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    state = state,
                    mode = mode,
                    onClick = {
                        handleIntent(ServerSetupIntent.UpdateServerMode(it))
                    },
                )
            }
        }
    }
}
