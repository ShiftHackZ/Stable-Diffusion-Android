package com.shifthackz.aisdv1.presentation.modal.ldscheduler

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.presentation.screen.debug.mapToUi
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

@Composable
@Preview
fun LDSchedulerBottomSheet(
    modifier: Modifier = Modifier,
    currentScheduler: SchedulersToken = SchedulersToken.COMPUTATION,
    onSelected: (SchedulersToken) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
    ) {
        SchedulersToken.entries.forEach { scheduler ->
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                selected = scheduler == currentScheduler,
                text = scheduler.mapToUi(),
                showChevron = false,
                onClick = { onSelected(scheduler) },
                startIcon = Icons.Default.Construction,
            )
        }
    }
}
