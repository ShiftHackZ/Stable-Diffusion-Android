@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.report

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.theme.isSdAppInDarkTheme
import com.shifthackz.aisdv1.presentation.widget.input.chip.ChipTextFieldItem
import com.shifthackz.android.core.mvi.MviComponent

@Composable
fun ReportScreen(
    viewModel: ReportViewModel,
    modifier: Modifier = Modifier,
) {
    MviComponent(
        viewModel = viewModel,
    ) { state, intentHandler ->
        ReportScreenContent(
            modifier = modifier,
            state = state,
            processIntent = intentHandler,
        )
    }
}

@Composable
@Preview(name = "Loading State")
private fun ReportScreenContent(
    modifier: Modifier = Modifier,
    state: ReportState = ReportState(),
    processIntent: (ReportIntent) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(LocalizationR.string.report_title),
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            processIntent(ReportIntent.NavigateBack)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back button",
                        )
                    }
                },
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .navigationBarsPadding()
                    .height(height = 60.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                onClick = {
                    val intent = if (state.reportSent) {
                        ReportIntent.NavigateBack
                    } else {
                        ReportIntent.Submit
                    }
                    processIntent(intent)
                },
                enabled = !state.loading,
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = if (state.reportSent) {
                        Icons.Default.Check
                    } else {
                        Icons.AutoMirrored.Filled.Send
                    },
                    contentDescription = "Send",
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(
                        if (state.reportSent) LocalizationR.string.report_done
                        else LocalizationR.string.report_submit
                    ),
                    color = LocalContentColor.current,
                )
            }
        },
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            targetState = !state.loading,
            label = "report_state_animator",
        ) { contentVisible ->
            if (contentVisible) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (state.reportSent) {
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier.size(100.dp),
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Done",
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                            text = stringResource(LocalizationR.string.report_sent),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        state.imageBitmap?.asImageBitmap()?.let {
                            Image(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth(0.45f)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(12.dp)),
                                bitmap = it,
                                contentScale = ContentScale.Crop,
                                contentDescription = "ai",
                            )
                        }
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                                .drawWithContent { drawContent() },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ReportReason.entries.forEach { reason ->
                                val resId: Int = when (reason) {
                                    ReportReason.InappropriateContent -> {
                                        LocalizationR.string.report_reason_inappropriate_content
                                    }

                                    ReportReason.Violence -> {
                                        LocalizationR.string.report_reason_violence
                                    }

                                    ReportReason.HatefulSpeech -> {
                                        LocalizationR.string.report_reason_hateful_speech
                                    }

                                    ReportReason.IntellectualPropertyInfringement -> {
                                        LocalizationR.string.report_reason_intellectual
                                    }

                                    ReportReason.AdultContent -> {
                                        LocalizationR.string.report_reason_adult
                                    }

                                    ReportReason.Other -> {
                                        LocalizationR.string.report_reason_other
                                    }
                                }
                                val isDark = isSdAppInDarkTheme()
                                ChipTextFieldItem(
                                    modifier = if (reason == state.reason) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = if (isDark) Color.White else Color.DarkGray,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                    } else {
                                        Modifier
                                    },
                                    innerPadding = PaddingValues(
                                        vertical = 2.dp,
                                        horizontal = 6.dp
                                    ),
                                    text = stringResource(resId),
                                    onItemClick = { processIntent(ReportIntent.UpdateReason(reason)) },
                                )
                            }
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            value = state.text,
                            onValueChange = { processIntent(ReportIntent.UpdateText(it)) },
                            label = {
                                Text(
                                    text = stringResource(LocalizationR.string.report_description),
                                )
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f),
                    )
                }
            }
        }
    }
    ModalRenderer(screenModal = state.screenModal) {
        (it as? ReportIntent)?.let(processIntent::invoke)
    }
}

@Composable
@Preview(name = "Content state")
private fun PreviewContent() {
    ReportScreenContent(
        state = ReportState(loading = false)
    )
}

@Composable
@Preview(name = "Sent state")
private fun PreviewSent() {
    ReportScreenContent(
        state = ReportState(loading = false, reportSent = true)
    )
}
