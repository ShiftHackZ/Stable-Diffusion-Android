package com.shifthackz.aisdv1.presentation.theme

import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Exposes the `sliderColors` value used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
val sliderColors: SliderColors
    @Composable get() = SliderDefaults.colors(
        activeTickColor = Color.Transparent,
        inactiveTickColor = Color.Transparent,
    )
