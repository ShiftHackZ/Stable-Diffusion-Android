package com.shifthackz.aisdv1.presentation.screen.inpaint.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun CapSizeSlider(
    modifier: Modifier = Modifier,
    size: Int = 16,
    onValueChanged: (Int) -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(2f)
                .aspectRatio(1f),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.defaultMinSize(
                    minWidth = 30.dp,
                    minHeight = 30.dp,
                ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(size.dp)
                        .background(Color.White)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                )
            }
        }

        Box(
            modifier = Modifier.weight(8f),
            contentAlignment = Alignment.Center,
        ) {
            Slider(
                value = size * 1f,
                valueRange = (Constants.DRAW_CAP_RANGE_MIN * 1f)..(Constants.DRAW_CAP_RANGE_MAX * 1f),
                steps = abs(Constants.DRAW_CAP_RANGE_MIN - Constants.DRAW_CAP_RANGE_MAX) - 1,
                colors = sliderColors.copy(
                    inactiveTrackColor = MaterialTheme.colorScheme.background
                ),
                onValueChange = { onValueChanged(it.roundToInt()) },
            )
        }
    }
}
