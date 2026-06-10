@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.report

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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

/**
 * Carries `ReportScreenContentState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class ReportScreenContentState(
    /**
     * Exposes the `loading` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loading: Boolean = true,
    /**
     * Exposes the `imageBase64` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val imageBase64: String = "",
    /**
     * Exposes the `text` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val text: String = "",
    /**
     * Exposes the `reason` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reason: ReportReason = ReportReason.Other,
    /**
     * Exposes the `reportSent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reportSent: Boolean = false,
)

/**
 * Carries `ReportScreenStrings` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class ReportScreenStrings(
    /**
     * Exposes the `title` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String,
    /**
     * Exposes the `done` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val done: String,
    /**
     * Exposes the `submit` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val submit: String,
    /**
     * Exposes the `sent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sent: String,
    /**
     * Exposes the `description` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val description: String,
    /**
     * Exposes the `backContentDescription` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val backContentDescription: String,
    /**
     * Exposes the `sendContentDescription` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sendContentDescription: String,
    /**
     * Exposes the `doneContentDescription` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val doneContentDescription: String,
    /**
     * Exposes the `imageContentDescription` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val imageContentDescription: String,
    /**
     * Exposes the `reasonInappropriateContent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reasonInappropriateContent: String,
    /**
     * Exposes the `reasonViolence` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reasonViolence: String,
    /**
     * Exposes the `reasonHatefulSpeech` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reasonHatefulSpeech: String,
    /**
     * Exposes the `reasonIntellectualProperty` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reasonIntellectualProperty: String,
    /**
     * Exposes the `reasonAdultContent` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reasonAdultContent: String,
    /**
     * Exposes the `reasonOther` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reasonOther: String,
)

/**
 * Renders the `ReportScreenContent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param onNavigateBack callback invoked by the component.
 * @param onSubmit callback invoked by the component.
 * @param onTextChange callback invoked by the component.
 * @param onReasonChange callback invoked by the component.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `ReportLoadedContent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param onTextChange callback invoked by the component.
 * @param onReasonChange callback invoked by the component.
 * @author Dmitriy Moroz
 */
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

/**
 * Executes the `label` step in the SDAI presentation layer.
 *
 * @param reason reason value consumed by the API.
 * @author Dmitriy Moroz
 */
private fun ReportScreenStrings.label(reason: ReportReason): String = when (reason) {
    ReportReason.InappropriateContent -> reasonInappropriateContent
    ReportReason.Violence -> reasonViolence
    ReportReason.HatefulSpeech -> reasonHatefulSpeech
    ReportReason.IntellectualPropertyInfringement -> reasonIntellectualProperty
    ReportReason.AdultContent -> reasonAdultContent
    ReportReason.Other -> reasonOther
}
