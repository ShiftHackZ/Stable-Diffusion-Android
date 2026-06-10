@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.report

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ReportReason
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar

@Immutable
data class ReportScreenContentState(
    val loading: Boolean = true,
    val imageBase64: String = "",
    val text: String = "",
    val reason: ReportReason = ReportReason.Other,
    val reportSent: Boolean = false,
)

data class ReportScreenStrings(
    val title: String,
    val done: String,
    val submit: String,
    val sent: String,
    val description: String,
    val backContentDescription: String,
    val sendContentDescription: String,
    val doneContentDescription: String,
    val imageContentDescription: String,
    val reasonInappropriateContent: String,
    val reasonViolence: String,
    val reasonHatefulSpeech: String,
    val reasonIntellectualProperty: String,
    val reasonAdultContent: String,
    val reasonOther: String,
)

@Composable
fun ReportScreenContent(
    state: ReportScreenContentState,
    strings: ReportScreenStrings,
    onNavigateBack: () -> Unit,
    onSubmit: () -> Unit,
    onTextChange: (String) -> Unit,
    onReasonChange: (ReportReason) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.imePadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = strings.title)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = strings.backContentDescription,
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
                    if (state.reportSent) onNavigateBack() else onSubmit()
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
                    contentDescription = strings.sendContentDescription,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = if (state.reportSent) strings.done else strings.submit,
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
                ReportLoadedContent(
                    state = state,
                    strings = strings,
                    onTextChange = onTextChange,
                    onReasonChange = onReasonChange,
                )
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
}

@Composable
private fun ReportLoadedContent(
    state: ReportScreenContentState,
    strings: ReportScreenStrings,
    onTextChange: (String) -> Unit,
    onReasonChange: (ReportReason) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScrollbar(scrollState)
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.reportSent) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Default.CheckCircle,
                contentDescription = strings.doneContentDescription,
            )
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                text = strings.sent,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            val imageBitmap = remember(state.imageBase64) {
                state.imageBase64.decodeBase64ImageBitmap()
            }
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.45f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    bitmap = imageBitmap,
                    contentScale = ContentScale.Crop,
                    contentDescription = strings.imageContentDescription,
                )
            }
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ReportReason.entries.forEach { reason ->
                    FilterChip(
                        selected = reason == state.reason,
                        onClick = { onReasonChange(reason) },
                        label = {
                            Text(text = strings.label(reason))
                        },
                    )
                }
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                value = state.text,
                onValueChange = onTextChange,
                label = {
                    Text(text = strings.description)
                },
                colors = textFieldColors,
            )
        }
    }
}

private fun ReportScreenStrings.label(reason: ReportReason): String = when (reason) {
    ReportReason.InappropriateContent -> reasonInappropriateContent
    ReportReason.Violence -> reasonViolence
    ReportReason.HatefulSpeech -> reasonHatefulSpeech
    ReportReason.IntellectualPropertyInfringement -> reasonIntellectualProperty
    ReportReason.AdultContent -> reasonAdultContent
    ReportReason.Other -> reasonOther
}
