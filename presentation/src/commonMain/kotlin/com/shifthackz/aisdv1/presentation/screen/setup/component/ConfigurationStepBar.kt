package com.shifthackz.aisdv1.presentation.screen.setup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState

/**
 * Shows the two-step provider setup progress indicator.
 *
 * @param currentStep selected step in the setup flow.
 * @param strings localized labels for the step captions.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ConfigurationStepBar(
    currentStep: ServerSetupState.Step,
    strings: ServerSetupStrings,
) {
    val circleSize = 36.dp
    val circleBorder = 2.dp
    val lineHeight = 4.dp
    val colorBg = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.34f)
    val colorAccent = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .padding(bottom = circleSize / 2),
        ) {
            val lineModifier = Modifier
                .height(lineHeight)
                .fillMaxWidth()
            Box(modifier = lineModifier.weight(0.5f))
            repeat(ServerSetupState.Step.entries.size - 1) { index ->
                Box(
                    modifier = lineModifier
                        .weight(1f)
                        .padding(horizontal = circleSize / 2 - circleBorder / 2)
                        .background(
                            color = if (currentStep.ordinal > index) {
                                colorAccent
                            } else {
                                colorBg
                            },
                        ),
                )
            }
            Box(modifier = lineModifier.weight(0.5f))
        }
        Row {
            ServerSetupState.Step.entries.forEach { step ->
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .clip(CircleShape)
                            .background(color = colorBg)
                            .border(
                                width = if (step.ordinal <= currentStep.ordinal) {
                                    circleBorder
                                } else {
                                    circleBorder / 2
                                },
                                color = colorAccent,
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        val icon = when {
                            step.ordinal < currentStep.ordinal -> Icons.Default.Check
                            step.ordinal == currentStep.ordinal -> Icons.Default.Circle
                            else -> null
                        }
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = when (step) {
                            ServerSetupState.Step.SOURCE -> strings.sourceTitle
                            ServerSetupState.Step.CONFIGURE -> strings.configureTitle
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (step == currentStep) colorAccent else Color.Unspecified,
                    )
                }
            }
        }
    }
}
