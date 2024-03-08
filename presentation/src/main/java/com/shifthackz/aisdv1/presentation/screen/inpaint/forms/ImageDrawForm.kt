package com.shifthackz.aisdv1.presentation.screen.inpaint.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintIntent
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintState
import com.shifthackz.aisdv1.presentation.screen.inpaint.components.InPaintComponent

@Composable
fun ImageDrawForm(
    modifier: Modifier = Modifier,
    state: InPaintState = InPaintState(),
    processIntent: (InPaintIntent) -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        InPaintComponent(
            drawMode = true,
            inPaint = state.model,
            bitmap = state.bitmap,
            capWidth = state.size,
            onPathDrawn = { processIntent(InPaintIntent.DrawPath(it)) },
            onPathBitmapDrawn = { processIntent(InPaintIntent.DrawPathBmp(it)) },
        )
    }
}
