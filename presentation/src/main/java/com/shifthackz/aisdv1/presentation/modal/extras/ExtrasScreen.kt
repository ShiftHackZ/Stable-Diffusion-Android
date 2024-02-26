package com.shifthackz.aisdv1.presentation.modal.extras

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DataExploration
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.widget.toolbar.ModalDialogToolbar

class ExtrasScreen(
    private val viewModel: ExtrasViewModel,
    private val onLoraSelected: (ExtraItemUi) -> Unit,
    private val onClose: () -> Unit,
) : MviScreen<ExtrasState, EmptyEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onLoraSelected = onLoraSelected,
            onClose = onClose,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ExtrasState,
    onLoraSelected: (ExtraItemUi) -> Unit,
    onClose: () -> Unit,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
    ) {
        Surface(
            modifier = modifier.fillMaxHeight(0.7f),
            shape = RoundedCornerShape(16.dp),
            color = AlertDialogDefaults.containerColor,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                ModalDialogToolbar(
                    text = when (state) {
                        is ExtrasState.Content -> when (state.type) {
                            ExtraType.Lora -> "Lora"
                            ExtraType.HyperNet -> "HyperNetwork"
                        }
                        else -> ""
                    },
                    onClose = onClose,
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    when (state) {
                        is ExtrasState.Content -> items(
                            count = state.loras.size,
                            key = { index -> state.loras[index].key },
                        ) { index ->
                            ExtrasItemComposable(
                                item = state.loras[index],
                                onLoraSelected = onLoraSelected,
                            )
                        }

                        ExtrasState.Loading -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtrasItemComposable(
    modifier: Modifier = Modifier,
    item: ExtraItemUi,
    onLoraSelected: (ExtraItemUi) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.background)
            .border(
                width = 2.dp,
                color = if (item.isApplied) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable { onLoraSelected(item) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.DataExploration,
            contentDescription = "Lora ${item.name}",
        )
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            item.alias?.let {
                Text(
                    text = "Alias: $it",
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        item.value?.let {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.requiredWidth(IntrinsicSize.Min),
                maxLines = 1,
                text = it,
            )
        }
    }
}
