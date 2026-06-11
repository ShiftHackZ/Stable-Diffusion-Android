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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin

/**
 * Renders the `BackgroundWorkWidget` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
fun BackgroundWorkWidget(
    modifier: Modifier = Modifier,
) {
    val koin = remember { initKoin() }
    val viewModel = remember(koin) {
        koin.get<BackgroundWorkViewModel>()
    }

    MviComponent(
        viewModel = viewModel,
    ) { state: BackgroundWorkState, processIntent: (BackgroundWorkIntent) -> Unit ->
        BackgroundWorkWidgetContent(
            modifier = modifier,
            state = state,
            processIntent = processIntent,
        )
    }
}

/**
 * Renders the `BackgroundWorkWidgetContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
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
                    if (state.visible && state.running && !state.isError && state.image == null) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(40.dp)
                                .aspectRatio(1f),
                        )
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.AutoFixNormal,
                            contentDescription = Localization.string("action_generate"),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        )
                    }
                    if (state.visible && !state.running && !state.isError && state.image == null) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = Localization.string("notification_finish_title"),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    if (state.visible && state.isError && state.image == null) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.Error,
                            contentDescription = Localization.string("error_generic"),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    state.image?.takeIf { !state.isError }?.let {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = it,
                            contentDescription = null,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = state.title.takeIf(String::isNotBlank)
                            ?: Localization.string("notification_pending_title"),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    state.subTitle.takeIf(String::isNotBlank)?.let { subTitle ->
                        Text(
                            text = subTitle,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                if (state.dismissible) {
                    TextButton(onClick = { processIntent(BackgroundWorkIntent.Dismiss) }) {
                        Text(
                            text = Localization.string("ok"),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}
