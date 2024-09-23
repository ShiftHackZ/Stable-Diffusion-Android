package com.shifthackz.aisdv1.presentation.widget.work

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.android.core.mvi.MviComponent
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun BackgroundWorkWidget(
    modifier: Modifier = Modifier,
) {
    MviComponent(
        viewModel = koinViewModel<BackgroundWorkViewModel>(),
    ) { state, processIntent ->
        BackgroundWorkWidgetContent(
            modifier = modifier,
            state = state,
            processIntent = processIntent,
        )
    }
}

@Composable
@Preview
private fun BackgroundWorkWidgetContent(
    modifier: Modifier = Modifier,
    state: BackgroundWorkState = BackgroundWorkState(),
    processIntent: (BackgroundWorkIntent) -> Unit = {},
) {
    AnimatedVisibility(
        modifier = modifier.fillMaxWidth(),
        visible = state.visible,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        val shape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(MaterialTheme.colorScheme.surfaceTint, shape)
                    .clip(shape)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (state.visible && !state.isError && state.bitmap == null) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(40.dp)
                                .aspectRatio(1f),
                        )
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.AutoFixNormal,
                            contentDescription = "Imagine",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        )
                    }
                    if (state.visible && state.isError && state.bitmap == null) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    state.bitmap?.takeIf { !state.isError }?.let {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                        )
                    }
                }
                Column {
                    Text(
                        text = state.title.asString().takeIf(String::isNotBlank)
                            ?: stringResource(id = LocalizationR.string.notification_pending_title),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    state.subTitle.asString().takeIf(String::isNotBlank)?.let { subTitle ->
                        Text(
                            text = subTitle,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                if (state.isError || state.bitmap != null) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = { processIntent(BackgroundWorkIntent.Dismiss) }) {
                        Text(
                            text = stringResource(id = LocalizationR.string.ok),
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun ContentPreview() {
    BackgroundWorkWidgetContent(
        state = BackgroundWorkState(
            true,
            "Header".asUiText(),
            "This is status message.\nThat is indeed multiline.".asUiText(),
        ),
    )
}
