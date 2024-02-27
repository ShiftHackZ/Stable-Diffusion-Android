package com.shifthackz.aisdv1.presentation.modal.embedding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasEffect
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.presentation.widget.error.ErrorComposable
import com.shifthackz.aisdv1.presentation.widget.toolbar.ModalDialogToolbar

class EmbeddingScreen(
    private val viewModel: EmbeddingViewModel,
    private val onNewPrompts: (String, String) -> Unit,
    private val onClose: () -> Unit,
) : MviScreen<EmbeddingState, ExtrasEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onClose = onClose,
            onSelectorChange = viewModel::changeSelector,
            onItemToggle = viewModel::toggleItem,
            onApplyNewPrompts = viewModel::applyNewPrompts,
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
    state: EmbeddingState,
    onClose: () -> Unit,
    onSelectorChange: (Boolean) -> Unit,
    onItemToggle: (EmbeddingItemUi) -> Unit,
    onApplyNewPrompts: () -> Unit,
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
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                ) {
                    ModalDialogToolbar(
                        text = stringResource(id = R.string.title_txt_inversion),
                        onClose = onClose,
                    )
                    val bgColor = MaterialTheme.colorScheme.surface
                    var dividerHeight by remember {
                        mutableIntStateOf(0)
                    }
                    if (!state.loading && state.embeddings.isNotEmpty()) {
                        Row(
                            modifier = modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxWidth()
                                .height(intrinsicSize = IntrinsicSize.Max)
                                .clip(RoundedCornerShape(16.dp))
                                .background(color = bgColor)
                                .onSizeChanged {
                                    if (it.height > dividerHeight) dividerHeight = it.height
                                },
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(with(LocalDensity.current) { dividerHeight.toDp() })
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color = if (state.selector) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onSelectorChange(true) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    text = stringResource(id = R.string.hint_prompt),
                                    color = if (state.selector) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current,
                                    textAlign = TextAlign.Center,
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color = if (!state.selector) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable { onSelectorChange(false) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    text = stringResource(id = R.string.hint_prompt_negative),
                                    color = if (!state.selector) MaterialTheme.colorScheme.onPrimary else LocalContentColor.current,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn {
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
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .shimmer(),
                                )
                            }
                        } else {
                            if (state.embeddings.isEmpty()) {
                                item(key = "empty_state") {
                                    EmbeddingEmptyState()
                                }
                            } else {
                                items(
                                    count = state.embeddings.size,
                                    key = { index -> state.embeddings[index].keyword },
                                ) { index ->
                                    EmbeddingItemComposable(
                                        item = state.embeddings[index],
                                        selector = state.selector,
                                        onItemToggle = onItemToggle,
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
private fun EmbeddingEmptyState() {
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
            text = stringResource(id = R.string.extras_empty_sub_title_embedding),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmbeddingItemComposable(
    item: EmbeddingItemUi,
    selector: Boolean,
    onItemToggle: (EmbeddingItemUi) -> Unit,
) {
    val isApplied = (selector && item.isInPrompt) || (!selector && item.isInNegativePrompt)
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 2.dp,
                color = if (isApplied) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(16.dp),
            )
            .clickable { onItemToggle(item) }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_text),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = item.keyword,
        )
    }
}
