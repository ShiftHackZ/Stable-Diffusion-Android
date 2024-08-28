package com.shifthackz.aisdv1.presentation.screen.setup.steps

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.components.ConfigurationModeButton
import kotlin.math.abs

@Composable
fun SourceSelectionStep(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit = {},
) {
    val lazyListState = rememberLazyListState()
    var lazyListHeight by remember { mutableIntStateOf(0) }
    var lazyListItemHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(state.mode) {
        // Adding 1 here, because item with index == 0 is top spacer
        val newIndex = state.mode.ordinal +1
        val visibleIndexes = lazyListState.layoutInfo.visibleItemsInfo
            .filter { it.offset >= 0 }
            .filter {
                if (lazyListHeight == 0 || lazyListItemHeight == 0) true
                else abs(lazyListHeight - it.offset) >= lazyListItemHeight
            }
            .map(LazyListItemInfo::index)

        if (!visibleIndexes.contains(newIndex)) lazyListState.animateScrollToItem(newIndex)
    }

    LazyColumn(
        modifier = modifier
            .onSizeChanged { lazyListHeight = it.height },
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
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .onSizeChanged { lazyListItemHeight = it.height },
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
