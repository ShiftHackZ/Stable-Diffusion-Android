package com.shifthackz.aisdv1.presentation.modal.tag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.input.chip.ChipTextFieldItem
import com.shifthackz.aisdv1.presentation.widget.toolbar.ModalDialogToolbar
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun EditTagDialog(
    modifier: Modifier = Modifier,
    prompt: String,
    negativePrompt: String,
    tag: String,
    isNegative: Boolean,
    onDismissRequest: () -> Unit,
    onNewPrompts: (String, String) -> Unit,
) {
    MviComponent(
        viewModel = koinViewModel<EditTagViewModel>().apply {
            processIntent(EditTagIntent.InitialData(prompt, negativePrompt, tag, isNegative))
        },
        processEffect = { effect ->
            when (effect) {
                EditTagEffect.Close -> onDismissRequest()

                is EditTagEffect.ApplyPrompts -> {
                    onNewPrompts(effect.prompt, effect.negativePrompt)
                    onDismissRequest()
                }
            }
        },
    ) { state, processIntent ->
        ScreenContent(
            modifier = modifier,
            state = state,
            processIntent = processIntent,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: EditTagState,
    processIntent: (EditTagIntent) -> Unit = {},
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        ),
    ) {
        Surface(
            modifier = modifier.fillMaxHeight(0.36f),
            shape = RoundedCornerShape(16.dp),
            color = AlertDialogDefaults.containerColor,
        ) {
            Scaffold(
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .fillMaxWidth(),
                    ) {
                        val localModifier = Modifier.weight(1f)
                        IconButton(
                            modifier = localModifier,
                            onClick = { processIntent(EditTagIntent.Action.Delete) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                            )
                        }
                        IconButton(
                            modifier = localModifier,
                            onClick = { processIntent(EditTagIntent.Action.Apply) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Apply",
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ModalDialogToolbar(
                        text = stringResource(id = LocalizationR.string.title_tag_edit),
                        onClose = { processIntent(EditTagIntent.Close) },
                    )
                    ChipTextFieldItem(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        type = state.extraType,
                        text = state.currentTag,
                        showDeleteIcon = false,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        value = state.currentTag,
                        onValueChange = { processIntent(EditTagIntent.UpdateTag(it)) },
                        enabled = state.extraType == null,
                        singleLine = true,
                        label = {
                            Text(
                                stringResource(
                                    id = when (state.extraType) {
                                        ExtraType.Lora -> LocalizationR.string.title_lora
                                        ExtraType.HyperNet -> LocalizationR.string.hint_hypernet
                                        null -> LocalizationR.string.hint_tag
                                    }
                                )
                            )
                        },
                    )
                    state.currentValue?.let {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            value = "$it",
                            onValueChange = {},
                            enabled = false,
                            singleLine = true,
                            label = { Text(stringResource(id = LocalizationR.string.hint_value)) },
                            trailingIcon = {
                                Row {
                                    val decEnabled = it > Constants.EXTRA_MINIMUM
                                    val incEnabled = it < Constants.EXTRA_MAXIMUM
                                    IconButton(
                                        enabled = decEnabled,
                                        onClick = {
                                            processIntent(EditTagIntent.Value.Decrement)
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = if (decEnabled) 1f else 0.5f,
                                            ),
                                        )
                                    }
                                    IconButton(
                                        enabled = incEnabled,
                                        onClick = {
                                            processIntent(EditTagIntent.Value.Increment)
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowUp,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = if (incEnabled) 1f else 0.5f,
                                            ),
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun ScreenContentPreview() {
    ScreenContent(
        state = EditTagState("", "", "tag", "tag", null, false)
    )
}
