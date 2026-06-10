package com.shifthackz.aisdv1.presentation.widget.input.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.presentation.utils.ExtrasFormatter

/**
 * Renders the `ChipTextFieldWithItem` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param textFieldValueState text field value state value consumed by the API.
 * @param label label value consumed by the API.
 * @param list list value consumed by the API.
 * @param onItemClick callback invoked by the component.
 * @param onEvent callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
fun ChipTextFieldWithItem(
    modifier: Modifier = Modifier,
    textFieldValueState: MutableState<TextFieldValue>,
    label: UiText,
    list: List<String>,
    onItemClick: (type: Int, item: String) -> Unit = { _, _ -> },
    onEvent: (event: ChipTextFieldEvent<String>) -> Unit,
) {
    ChipTextField(
        modifier = modifier,
        label = { Text(label.asString()) },
        textFieldValueState = textFieldValueState,
        textStyle = LocalTextStyle.current,
        chips = list,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        chipEventListener = { onEvent(it) },
    ) { index, item ->
        ChipTextFieldItem(
            text = item,
            type = ExtrasFormatter.determineExtraType(item),
            showDeleteIcon = true,
            onDeleteClick = { onEvent(ChipTextFieldEvent.Remove(index)) },
            onItemClick = {
                onItemClick(index, item)
            }
        )
    }
}
