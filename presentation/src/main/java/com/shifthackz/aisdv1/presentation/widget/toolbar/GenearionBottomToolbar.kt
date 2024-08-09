package com.shifthackz.aisdv1.presentation.widget.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.GenerationMviState
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun GenerationBottomToolbar(
    modifier: Modifier = Modifier,
    state: GenerationMviState,
    strokeAccentState: Boolean = false,
    processIntent: (GenerationMviIntent) -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(top = 8.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        when (state.mode) {
            ServerSource.AUTOMATIC1111,
            ServerSource.SWARM_UI -> {
                GenerationBottomToolbarBottomLayer(
                    modifier = Modifier.padding(bottom = 36.dp),
                    strokeAccentState = strokeAccentState,
                    state = state,
                    processIntent = processIntent,
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(horizontal = 16.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 22.dp,
                                topEnd = 22.dp,
                            )
                        )
                        .background(color = MaterialTheme.colorScheme.surface),
                )
            }
            else -> Unit
        }
        content()
    }
}

@Composable
private fun GenerationBottomToolbarBottomLayer(
    modifier: Modifier = Modifier,
    state: GenerationMviState,
    strokeAccentState: Boolean = false,
    processIntent: (GenerationMviIntent) -> Unit = {},
) {
    val shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
    )
    val accentColor = if (strokeAccentState)

        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    var dividerHeight by remember {
        mutableIntStateOf(0)
    }
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clip(shape)
            .border(
                width = 1.dp,
                color = accentColor,
                shape = shape,
            )
            .onSizeChanged {
                dividerHeight = it.height
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        fun localModifier(click: () -> Unit) = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterVertically)
            .clickable { click() }
            .weight(1f)
            .padding(vertical = 8.dp)
            .padding(bottom = 26.dp)

        val localColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
        val localStyle = MaterialTheme.typography.labelLarge

        Text(
            modifier = localModifier {
                processIntent(
                    GenerationMviIntent.SetModal(
                        Modal.ExtraBottomSheet(
                            state.prompt,
                            state.negativePrompt,
                            ExtraType.Lora,
                        ),
                    ),
                )
            },
            text = stringResource(id = LocalizationR.string.title_lora),
            textAlign = TextAlign.Center,
            color = localColor,
            style = localStyle,
        )
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .height(with(LocalDensity.current) { dividerHeight.toDp() })
                .background(color = accentColor),
        )
        Text(
            modifier = localModifier {
                processIntent(
                    GenerationMviIntent.SetModal(
                        Modal.Embeddings(
                            state.prompt,
                            state.negativePrompt,
                        )
                    )
                )
            },
            text = stringResource(id = LocalizationR.string.title_txt_inversion_short),
            textAlign = TextAlign.Center,
            color = localColor,
            style = localStyle,
        )
        if (state.mode == ServerSource.AUTOMATIC1111) {
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(with(LocalDensity.current) { dividerHeight.toDp() })
                    .background(color = accentColor),
            )
            Text(
                modifier = localModifier {
                    processIntent(
                        GenerationMviIntent.SetModal(
                            Modal.ExtraBottomSheet(
                                state.prompt,
                                state.negativePrompt,
                                ExtraType.HyperNet,
                            ),
                        ),
                    )
                },
                text = stringResource(id = LocalizationR.string.title_hyper_net_short),
                textAlign = TextAlign.Center,
                color = localColor,
                style = localStyle,
            )
        }
    }
}
