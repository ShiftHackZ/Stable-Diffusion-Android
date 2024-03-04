package com.shifthackz.aisdv1.presentation.widget.input.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun ChipTextFieldWithItem(
    modifier: Modifier = Modifier,
    textFieldValueState: MutableState<TextFieldValue>,
    label: Int,
    list: List<String>,
    onEvent: (event: ChipTextFieldEvent<String>) -> Unit,
) {
    ChipTextField(
        modifier = modifier,
        label = { Text(stringResource(id = label)) },
        textFieldValueState = textFieldValueState,
        textStyle = LocalTextStyle.current,
        chips = list,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        chipEventListener = { onEvent(it) },
    ) { index, item ->
        ChipTextFieldItem(
            text = item,
            showDeleteIcon = true,
            onDeleteClick = { onEvent(ChipTextFieldEvent.Remove(index)) },
        )
    }
}
