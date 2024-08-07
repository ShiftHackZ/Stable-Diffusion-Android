package com.shifthackz.aisdv1.presentation.screen.setup.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ConfigurationStepBar(
    modifier: Modifier = Modifier,
    currentStep: ServerSetupState.Step,
) {
    val circleSize = 36.dp
    val circleBorder = 2.dp
    val lineHeight = 4.dp
    val colorBg = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.34f)
    val colorAccent = MaterialTheme.colorScheme.primary
    Box(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .padding(bottom = circleSize / 2),
        ) {
            val lineMod = Modifier
                .height(lineHeight)
                .fillMaxWidth()
            Box(modifier = lineMod.weight(0.5f))
            (0 until ServerSetupState.Step.entries.size - 1).forEach { index ->
                Box(
                    modifier = lineMod
                        .weight(1f)
                        .padding(horizontal = circleSize / 2 - circleBorder / 2)
                        .background(color = when {
                            currentStep.ordinal > index -> colorAccent
                            else -> colorBg
                        }),
                )
            }
            Box(modifier = lineMod.weight(0.5f))
        }
        Row {
            val localModifier = Modifier.weight(1f)
            ServerSetupState.Step.entries.forEach { step ->
                val localShape = CircleShape
                Column(
                    modifier = localModifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .clip(localShape)
                            .background(color = colorBg)
                            .border(
                                width = when {
                                    step.ordinal <= currentStep.ordinal -> circleBorder
                                    else -> circleBorder / 2
                                },
                                color = colorAccent,
                                shape = localShape
                            ),
                        contentAlignment = Alignment.Center
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
                        text = stringResource(
                            id = when (step) {
                                ServerSetupState.Step.SOURCE -> LocalizationR.string.srv_step_1
                                ServerSetupState.Step.CONFIGURE -> LocalizationR.string.srv_step_2
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (step.ordinal == currentStep.ordinal) {
                            colorAccent
                        } else {
                            Color.Unspecified
                        },
                    )
                }
            }
        }
    }

}

@Composable
@Preview
private fun PreviewStep1() {
    ConfigurationStepBar(currentStep = ServerSetupState.Step.SOURCE)

}

@Composable
@Preview
private fun PreviewStep2() {
    ConfigurationStepBar(currentStep = ServerSetupState.Step.CONFIGURE)
}
