@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.widget.input.chip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import org.apache.commons.lang3.StringUtils

sealed interface ChipTextFieldEvent<T : Any> {

    data class Add<T : Any>(val item: T) : ChipTextFieldEvent<T>

    data class AddBatch<T : Any>(val items: List<T>) : ChipTextFieldEvent<T>

    data class Update<T : Any>(val index: Int, val item: T) : ChipTextFieldEvent<T>

    data class Remove<T : Any>(val index: Int) : ChipTextFieldEvent<T>
}

@Composable
fun ChipTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    textFieldValueState: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) },
    chips: List<String> = remember { emptyList() },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    chipSeparatorChar: Char = ',',
    chipEventListener: (event: ChipTextFieldEvent<String>) -> Unit,
    chipContent: @Composable (index: Int, item: String) -> Unit,
) {
    ChipTextField(
        modifier = modifier,
        label = label,
        textStyle = textStyle,
        textFieldValueState = textFieldValueState,
        chips = chips,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        maxItemsInEachRow = maxItemsInEachRow,
        chipSeparatorChar = chipSeparatorChar,
        chipTextToItemMapper = { it },
        chipItemToTextMapper = { it },
        chipEventListener = chipEventListener,
        chipContent = chipContent,
    )
}

@Composable
fun <T : Any> ChipTextField(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    textStyle: TextStyle = TextStyle.Default,
    textFieldValueState: MutableState<TextFieldValue> = remember { mutableStateOf(TextFieldValue()) },
    chips: List<T> = remember { emptyList() },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    maxItemsInEachRow: Int = Int.MAX_VALUE,
    chipSeparatorChar: Char = ',',
    chipTextToItemMapper: (String) -> T,
    chipItemToTextMapper: (T) -> String,
    chipEventListener: (event: ChipTextFieldEvent<T>) -> Unit,
    chipContent: @Composable (index: Int, item: T) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = { focusRequester.requestFocus() },
        )
    ) {
        TextFieldDefaults.DecorationBox(
            label = label,
            value = textFieldValueState.value.text + if (chips.isNotEmpty()) " " else "",
            innerTextField = {
                FlowRow(
                    modifier = Modifier.drawWithContent { drawContent() },
                    horizontalArrangement = horizontalArrangement,
                    verticalArrangement = verticalArrangement,
                    maxItemsInEachRow = maxItemsInEachRow,
                ) {
                    chips.forEachIndexed { index, item ->
                        key(index) {
                            chipContent(index, item)
                        }
                    }

                    Box(
                        modifier = Modifier
                            // This minimum width that TextField can have
                            // if remaining space in same row is smaller it's moved to next line
                            .widthIn(min = 80.dp)
                            // TextField can grow as big as Composable width
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        BasicTextField(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .onKeyEvent {
                                    if (it.key == Key.Backspace
                                        && chips.isNotEmpty()
                                        && textFieldValueState.value.text.isEmpty()
                                    ) {
                                        val index = chips.size - 1
                                        val item = chips[index]
                                        val textString = chipItemToTextMapper(item)
                                        textFieldValueState.value = TextFieldValue(
                                            text = textString,
                                            selection = TextRange(textString.length)
                                        )
                                        chipEventListener(ChipTextFieldEvent.Remove(index))
                                        true
                                    } else {
                                        it.key == Key.Backspace
                                    }
                                },
                            interactionSource = interactionSource,
                            value = textFieldValueState.value,
                            textStyle = textStyle,
                            singleLine = true,
                            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                            onValueChange = { value ->
                                if (value.text.split(chipSeparatorChar).size > 2) {
                                    val diff = StringUtils.difference(
                                        textFieldValueState.value.text,
                                        value.text,
                                    )
                                    val newTags = diff.split(chipSeparatorChar)
                                        .map { it.trim() }
                                        .filter { it.isNotEmpty() }
                                    if (newTags.isNotEmpty()) {
                                        chipEventListener(
                                            ChipTextFieldEvent.AddBatch(
                                                newTags.map { chipTextToItemMapper(it) }
                                            )
                                        )
                                    }
                                } else {
                                    if (textFieldValueState.value.text.isNotEmpty()
                                        && value.text.lastOrNull() == chipSeparatorChar
                                    ) {
                                        chipEventListener(
                                            ChipTextFieldEvent.Add(
                                                item = chipTextToItemMapper(
                                                    value.text.trim().replace(
                                                        chipSeparatorChar.toString(), ""
                                                    )
                                                )
                                            )
                                        )
                                        textFieldValueState.value = TextFieldValue("")
                                    } else if (value.text.lastOrNull() != chipSeparatorChar) {
                                        textFieldValueState.value = value
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (textFieldValueState.value.text.isNotEmpty()) {
                                        chipEventListener(
                                            ChipTextFieldEvent.Add(
                                                item = chipTextToItemMapper(
                                                    textFieldValueState.value.text.trim().replace(
                                                        chipSeparatorChar.toString(), ""
                                                    )
                                                )
                                            )
                                        )
                                        textFieldValueState.value = TextFieldValue("")

                                    }
                                    focusRequester.freeFocus()
                                    keyboardController?.hide()
                                }
                            ),
                        )
                    }
                }
            },
            enabled = true,
            singleLine = false,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            colors = textFieldColors,
        )
    }
}
