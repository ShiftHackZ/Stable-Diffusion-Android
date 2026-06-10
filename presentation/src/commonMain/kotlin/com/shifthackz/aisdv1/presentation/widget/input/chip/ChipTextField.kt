@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.widget.input.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
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

/**
 * Defines the `ChipTextFieldEvent` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ChipTextFieldEvent<T : Any> {

    /**
     * Carries `Add` data through the SDAI presentation layer.
     *
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Add<T : Any>(val item: T) : ChipTextFieldEvent<T>

    /**
     * Carries `AddBatch` data through the SDAI presentation layer.
     *
     * @param items items value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class AddBatch<T : Any>(val items: List<T>) : ChipTextFieldEvent<T>

    /**
     * Carries `Update` data through the SDAI presentation layer.
     *
     * @param index index value consumed by the API.
     * @param item item value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Update<T : Any>(val index: Int, val item: T) : ChipTextFieldEvent<T>

    /**
     * Carries `Remove` data through the SDAI presentation layer.
     *
     * @param index index value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class Remove<T : Any>(val index: Int) : ChipTextFieldEvent<T>
}

/**
 * Renders the `ChipTextField` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param label label value consumed by the API.
 * @param textStyle text style value consumed by the API.
 * @param textFieldValueState text field value state value consumed by the API.
 * @param chips chips value consumed by the API.
 * @param horizontalArrangement horizontal arrangement value consumed by the API.
 * @param verticalArrangement vertical arrangement value consumed by the API.
 * @param maxItemsInEachRow max items in each row value consumed by the API.
 * @param chipSeparatorChar chip separator char value consumed by the API.
 * @param chipEventListener chip event listener value consumed by the API.
 * @param chipContent chip content value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `ChipTextField` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param label label value consumed by the API.
 * @param textStyle text style value consumed by the API.
 * @param textFieldValueState text field value state value consumed by the API.
 * @param chips chips value consumed by the API.
 * @param horizontalArrangement horizontal arrangement value consumed by the API.
 * @param verticalArrangement vertical arrangement value consumed by the API.
 * @param maxItemsInEachRow max items in each row value consumed by the API.
 * @param chipSeparatorChar chip separator char value consumed by the API.
 * @param chipTextToItemMapper chip text to item mapper value consumed by the API.
 * @param chipItemToTextMapper chip item to text mapper value consumed by the API.
 * @param chipEventListener chip event listener value consumed by the API.
 * @param chipContent chip content value consumed by the API.
 * @author Dmitriy Moroz
 */
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
    val shape = RoundedCornerShape(4.dp)

    Box(
        modifier = modifier
            .background(Color.Unspecified, shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable(
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
                                    val diff = value.text.differenceFrom(
                                        textFieldValueState.value.text,
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

/**
 * Executes the `differenceFrom` step in the SDAI presentation layer.
 *
 * @param previous previous value consumed by the API.
 * @return Result produced by `differenceFrom`.
 * @author Dmitriy Moroz
 */
private fun String.differenceFrom(previous: String): String {
    val commonPrefixLength = previous
        .zip(this)
        .takeWhile { (old, new) -> old == new }
        .size
    return substring(commonPrefixLength)
}
