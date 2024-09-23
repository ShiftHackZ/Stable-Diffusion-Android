package com.shifthackz.aisdv1.presentation.widget.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.localization.R
import com.shifthackz.aisdv1.presentation.theme.textFieldColors

@Composable
fun SliderTextInputField(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Double> = 0.0..1.0,
    valueDiff: Double = 1.0,
    fractionDigits: Int = 2,
    steps: Int = 0,
    sliderColors: SliderColors = SliderDefaults.colors(),
) = SliderTextInputField(
    value = value.toFloat(),
    onValueChange = { onValueChange(it.toDouble()) },
    modifier = modifier,
    valueRange = (valueRange.start.toFloat()) .. (valueRange.endInclusive.toFloat()),
    valueDiff = valueDiff.toFloat(),
    fractionDigits = fractionDigits,
    steps = steps,
    sliderColors = sliderColors,
)

@Composable
fun SliderTextInputField(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    valueDiff: Float = 1f,
    fractionDigits: Int = 2,
    steps: Int = 0,
    sliderColors: SliderColors = SliderDefaults.colors(),
) {
    var initialized by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val callback: (Float) -> Unit = { newValue ->
        newValue
            .coerceIn(valueRange)
            .roundTo(fractionDigits)
            .also { text = "$it" }
            .let(onValueChange::invoke)
    }
    val isNotApplied = text.toFloatOrNull()?.roundTo(fractionDigits) != value.roundTo(fractionDigits)
    val isValid = text.isNotBlank() && (text.toFloatOrNull()?.let { it in valueRange} ?: false)
    LaunchedEffect(Unit) {
        if (!initialized) {
            text = value.roundTo(fractionDigits).toString()
            initialized = true
        }
    }
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = if (fractionDigits != 0) text else text.replace(".0", ""),
            onValueChange = { newText ->
                text = newText
            },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            label = { Text(stringResource(id = R.string.hint_value)) },
            trailingIcon = {
                if (isNotApplied) {
                    Button(
                        onClick = { text.toFloatOrNull()?.let(callback::invoke) },
                        enabled = isValid,
                    ) {
                        Icon(
                            modifier = Modifier.padding(end = 4.dp),
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = LocalContentColor.current
                        )
                        Text(
                            text = "Apply",
                            color = LocalContentColor.current
                        )
                    }
                } else {
                    Row {
                        val decEnabled = value > valueRange.start
                        val incEnabled = value < valueRange.endInclusive
                        IconButton(
                            enabled = decEnabled,
                            onClick = { callback(value - valueDiff) },
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
                            onClick = { callback(value + valueDiff) },
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
            },
            colors = textFieldColors,
        )
        Slider(
            value = value,
            valueRange = valueRange,
            steps = steps,
            colors = sliderColors,
            onValueChange = { callback(it) },
        )
    }
}
