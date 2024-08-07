package com.shifthackz.aisdv1.presentation.widget.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.presentation.model.ErrorState
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    state: ErrorState,
) {
    if (state is ErrorState.None) return
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = LocalizationR.string.error_title),
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = when (state) {
                is ErrorState.WithMessage -> state.message.asString()
                else -> stringResource(id = LocalizationR.string.error_generic)
            },
            textAlign = TextAlign.Center,
        )
    }
}
