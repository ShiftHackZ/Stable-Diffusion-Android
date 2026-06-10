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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagConstants.EXTRA_MAXIMUM
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagConstants.EXTRA_MINIMUM
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagConstants.EXTRA_STEP
import com.shifthackz.aisdv1.presentation.widget.input.SliderTextInputField
import com.shifthackz.aisdv1.presentation.widget.input.chip.ChipTextFieldItem
import com.shifthackz.aisdv1.presentation.widget.toolbar.ModalDialogToolbar
import kotlin.math.abs
import org.koin.core.parameter.parametersOf

/**
 * Renders the `EditTagDialog` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param prompt positive prompt text for image generation.
 * @param negativePrompt negative prompt text for image generation.
 * @param tag tag value consumed by the API.
 * @param isNegative is negative value consumed by the API.
 * @param onDismissRequest callback invoked by the component.
 * @param onNewPrompts callback invoked by the component.
 * @author Dmitriy Moroz
 */
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
    val koin = remember { initKoin() }
    val viewModel = remember(
        koin,
        prompt,
        negativePrompt,
        tag,
        isNegative,
    ) {
        koin.get<EditTagViewModel> {
            parametersOf(prompt, negativePrompt, tag, isNegative)
        }
    }
    MviComponent(
        viewModel = viewModel,
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

/**
 * Renders the `ScreenContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
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
            modifier = modifier.fillMaxHeight(0.38f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
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
                                contentDescription = Localization.string("delete"),
                            )
                        }
                        IconButton(
                            modifier = localModifier,
                            onClick = { processIntent(EditTagIntent.Action.Apply) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = Localization.string("apply"),
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
                        text = Localization.string("title_tag_edit"),
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
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        value = state.currentTag,
                        onValueChange = { processIntent(EditTagIntent.UpdateTag(it)) },
                        enabled = state.extraType == null,
                        singleLine = true,
                        label = {
                            Text(
                                Localization.string(
                                    when (state.extraType) {
                                        ExtraType.Lora -> "title_lora"
                                        ExtraType.HyperNet -> "hint_hypernet"
                                        null -> "hint_tag"
                                    }
                                )
                            )
                        },
                        colors = textFieldColors,
                    )
                    state.currentValue?.let { value ->
                        SliderTextInputField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            value = value,
                            onValueChange = { processIntent(EditTagIntent.UpdateValue(it)) },
                            valueRange = EXTRA_MINIMUM .. EXTRA_MAXIMUM,
                            valueDiff = EXTRA_STEP,
                            steps = abs(EXTRA_MAXIMUM.toInt() - EXTRA_MINIMUM.toInt()) * 4 - 1,
                            sliderColors = sliderColors,
                        )
                    }
                }
            }
        }
    }
}
