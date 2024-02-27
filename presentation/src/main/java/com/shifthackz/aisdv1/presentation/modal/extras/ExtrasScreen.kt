package com.shifthackz.aisdv1.presentation.modal.extras

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataExploration
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.widget.error.ErrorComposable
import com.shifthackz.aisdv1.presentation.widget.toolbar.ModalDialogToolbar

class ExtrasScreen(
    private val viewModel: ExtrasViewModel,
    private val onNewPrompts: (String, String) -> Unit,
    private val onClose: () -> Unit,
) : MviScreen<ExtrasState, ExtrasEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onItemToggle = viewModel::toggleItem,
            onApplyNewPrompts = viewModel::applyNewPrompts,
            onClose = onClose,
        )
    }

    override fun processEffect(effect: ExtrasEffect) = when (effect) {
        is ExtrasEffect.ApplyPrompts -> {
            onNewPrompts(effect.prompt, effect.negativePrompt)
            onClose()
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ExtrasState,
    onItemToggle: (ExtraItemUi) -> Unit,
    onApplyNewPrompts: () -> Unit,
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
            Scaffold(
                bottomBar = {
                    if (!state.loading) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            onClick = onApplyNewPrompts
                        ) {
                            Text(
                                text = stringResource(
                                    id = if (state.error != ErrorState.None) R.string.close
                                    else R.string.apply
                                ),
                                color = LocalContentColor.current,
                            )
                        }
                    }
                }
            ) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .fillMaxSize(),
                ) {
                    ModalDialogToolbar(
                        text = stringResource(id = when (state.type) {
                            ExtraType.Lora -> R.string.title_lora
                            ExtraType.HyperNet -> R.string.title_hyper_net
                        }),
                        onClose = onClose,
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (state.error != ErrorState.None) {
                            item(key = "error_state") {
                                ErrorComposable(state = state.error)
                            }
                        } else if (state.loading) {
                            items(
                                count = 3,
                                key = { index -> "shimmer_$index" },
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .shimmer(),
                                )
                            }
                        } else {
                            if (state.loras.isEmpty()) {
                                item(key = "empty_state") {
                                    ExtrasEmptyState(type = state.type)
                                }
                            } else {
                                items(
                                    count = state.loras.size,
                                    key = { index -> state.loras[index].key },
                                ) { index ->
                                    ExtrasItemComposable(
                                        item = state.loras[index],
                                        onLoraSelected = onItemToggle,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExtrasEmptyState(type: ExtraType) {
    Column(
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.extras_empty_title),
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = when (type) {
                ExtraType.Lora -> R.string.extras_empty_sub_title_lora
                ExtraType.HyperNet -> R.string.extras_empty_sub_title_hypernet
            }),
            textAlign = TextAlign.Center,
        )
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
            .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
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
