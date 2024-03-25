package com.shifthackz.aisdv1.presentation.screen.setup.steps

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.components.ConfigurationModeButton

@Composable
fun SourceSelectionStep(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    LaunchedEffect(state.mode) {
        // Adding 1 here, because item with index == 0 is top spacer
        lazyListState.animateScrollToItem(state.mode.ordinal + 1)
    }
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
    ) {
        item(key = "SPACER_TOP") { Spacer(modifier = Modifier.height(12.dp)) }
        items(
            items = state.allowedModes,
            key = ServerSource::key,
        ) { mode ->
            ConfigurationModeButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                state = state,
                mode = mode,
                onClick = {
                    processIntent(ServerSetupIntent.UpdateServerMode(it))
                },
            )
        }
        item(key = "SPACER_BOTTOM") { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
