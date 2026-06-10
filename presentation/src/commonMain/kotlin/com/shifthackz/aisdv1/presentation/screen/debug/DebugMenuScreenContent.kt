@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CancelScheduleSend
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar

/**
 * Carries `DebugMenuScreenStrings` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class DebugMenuScreenStrings(
    /**
     * Exposes the `title` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String,
    /**
     * Exposes the `mainSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mainSection: String,
    /**
     * Exposes the `actionLogger` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionLogger: String,
    /**
     * Exposes the `actionLoggerClear` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionLoggerClear: String,
    /**
     * Exposes the `workManagerSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val workManagerSection: String,
    /**
     * Exposes the `actionWorkRestartTxt2Img` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionWorkRestartTxt2Img: String,
    /**
     * Exposes the `actionWorkRestartImg2Img` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionWorkRestartImg2Img: String,
    /**
     * Exposes the `actionWorkCancelAll` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionWorkCancelAll: String,
    /**
     * Exposes the `localDiffusionSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionSection: String,
    /**
     * Exposes the `actionLocalDiffusionAllowCancel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionLocalDiffusionAllowCancel: String,
    /**
     * Exposes the `actionLocalDiffusionScheduler` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionLocalDiffusionScheduler: String,
    /**
     * Exposes the `qualityAssuranceSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val qualityAssuranceSection: String,
    /**
     * Exposes the `actionBadBase64` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val actionBadBase64: String,
    /**
     * Exposes the `backContentDescription` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val backContentDescription: String,
)

/**
 * Carries `DebugMenuScreenContentState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class DebugMenuScreenContentState(
    /**
     * Exposes the `localDiffusionAllowCancel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionAllowCancel: Boolean = false,
    /**
     * Exposes the `localDiffusionSchedulerThread` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionSchedulerThread: String = "",
    /**
     * Exposes the `showWorkManagerSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showWorkManagerSection: Boolean = true,
    /**
     * Exposes the `showLocalDiffusionSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showLocalDiffusionSection: Boolean = true,
    /**
     * Exposes the `showQualityAssuranceSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showQualityAssuranceSection: Boolean = true,
)

/**
 * Defines the `DebugMenuAction` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DebugMenuAction {
    /**
     * Provides the `NavigateBack` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object NavigateBack : DebugMenuAction
    /**
     * Provides the `ViewLogs` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ViewLogs : DebugMenuAction
    /**
     * Provides the `ClearLogs` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ClearLogs : DebugMenuAction
    /**
     * Provides the `RestartTxt2Img` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object RestartTxt2Img : DebugMenuAction
    /**
     * Provides the `RestartImg2Img` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object RestartImg2Img : DebugMenuAction
    /**
     * Provides the `CancelAllWork` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object CancelAllWork : DebugMenuAction
    /**
     * Provides the `ToggleLocalDiffusionCancel` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object ToggleLocalDiffusionCancel : DebugMenuAction
    /**
     * Provides the `RequestLocalDiffusionScheduler` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object RequestLocalDiffusionScheduler : DebugMenuAction
    /**
     * Provides the `InsertBadBase64` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object InsertBadBase64 : DebugMenuAction
}

/**
 * Renders the `DebugMenuScreenContent` UI for the SDAI presentation layer.
 *
 * @param strings strings value consumed by the API.
 * @param state state rendered or processed by the component.
 * @param processAction process action value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
fun DebugMenuScreenContent(
    strings: DebugMenuScreenStrings,
    state: DebugMenuScreenContentState,
    processAction: (DebugMenuAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { processAction(DebugMenuAction.NavigateBack) },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = strings.backContentDescription,
                            )
                        },
                    )
                },
            )
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            val headerModifier = Modifier.padding(vertical = 16.dp)
            val itemModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)

            DebugMenuHeader(
                modifier = headerModifier,
                text = strings.mainSection,
            )
            DebugMenuItem(
                modifier = itemModifier,
                startIcon = Icons.AutoMirrored.Filled.TextSnippet,
                text = strings.actionLogger,
                onClick = { processAction(DebugMenuAction.ViewLogs) },
            )
            DebugMenuItem(
                modifier = itemModifier,
                startIcon = Icons.Default.CleaningServices,
                text = strings.actionLoggerClear,
                onClick = { processAction(DebugMenuAction.ClearLogs) },
            )

            if (state.showWorkManagerSection) {
                DebugMenuHeader(
                    modifier = headerModifier,
                    text = strings.workManagerSection,
                )
                DebugMenuItem(
                    modifier = itemModifier,
                    startIcon = Icons.Default.Refresh,
                    text = strings.actionWorkRestartTxt2Img,
                    onClick = { processAction(DebugMenuAction.RestartTxt2Img) },
                )
                DebugMenuItem(
                    modifier = itemModifier,
                    startIcon = Icons.Default.Refresh,
                    text = strings.actionWorkRestartImg2Img,
                    onClick = { processAction(DebugMenuAction.RestartImg2Img) },
                )
                DebugMenuItem(
                    modifier = itemModifier,
                    startIcon = Icons.Default.Cancel,
                    text = strings.actionWorkCancelAll,
                    onClick = { processAction(DebugMenuAction.CancelAllWork) },
                )
            }

            if (state.showLocalDiffusionSection) {
                DebugMenuHeader(
                    modifier = headerModifier,
                    text = strings.localDiffusionSection,
                )
                DebugMenuItem(
                    modifier = itemModifier,
                    startIcon = Icons.Default.CancelScheduleSend,
                    text = strings.actionLocalDiffusionAllowCancel,
                    onClick = { processAction(DebugMenuAction.ToggleLocalDiffusionCancel) },
                    showChevron = false,
                    endValueContent = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checked = state.localDiffusionAllowCancel,
                            onCheckedChange = {
                                processAction(DebugMenuAction.ToggleLocalDiffusionCancel)
                            },
                        )
                    },
                )
                DebugMenuItem(
                    modifier = itemModifier,
                    startIcon = Icons.Default.Construction,
                    text = strings.actionLocalDiffusionScheduler,
                    onClick = { processAction(DebugMenuAction.RequestLocalDiffusionScheduler) },
                    endValueText = state.localDiffusionSchedulerThread,
                )
            }

            if (state.showQualityAssuranceSection) {
                DebugMenuHeader(
                    modifier = headerModifier,
                    text = strings.qualityAssuranceSection,
                )
                DebugMenuItem(
                    modifier = itemModifier,
                    startIcon = Icons.Default.SettingsEthernet,
                    text = strings.actionBadBase64,
                    onClick = { processAction(DebugMenuAction.InsertBadBase64) },
                )
            }
        }
    }
}

/**
 * Renders the `DebugMenuHeader` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
private fun DebugMenuHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderLine()
        Text(
            modifier = Modifier.padding(horizontal = 8.dp),
            text = text,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
        )
        HeaderLine(inverse = true)
    }
}

/**
 * Renders the `HeaderLine` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param inverse inverse value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun RowScope.HeaderLine(
    modifier: Modifier = Modifier,
    inverse: Boolean = false,
) {
    val color = MaterialTheme.colorScheme.primary.copy(alpha = if (inverse) 0.25f else 0.7f)
    Spacer(
        modifier = modifier
            .weight(1f)
            .height(1.dp)
            .padding(vertical = 6.dp)
            .background(color = color),
    )
}

/**
 * Renders the `DebugMenuItem` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param startIcon start icon value consumed by the API.
 * @param showChevron show chevron value consumed by the API.
 * @param endValueText end value text value consumed by the API.
 * @param endValueContent end value content value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @author Dmitriy Moroz
 */
@Composable
private fun DebugMenuItem(
    text: String,
    modifier: Modifier = Modifier,
    startIcon: ImageVector? = null,
    showChevron: Boolean = true,
    endValueText: String = "",
    endValueContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
            .defaultMinSize(minHeight = 50.dp)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = endValueContent
                ?.let { Modifier.fillMaxWidth(0.8f) }
                ?: Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            startIcon?.let {
                Icon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    imageVector = it,
                    contentDescription = null,
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            endValueContent?.invoke() ?: run {
                if (endValueText.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = endValueText,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Right,
                    )
                }
                if (showChevron) {
                    Icon(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        imageVector = Icons.Default.ChevronRight,
                        tint = LocalContentColor.current,
                        contentDescription = null,
                    )
                }
            }
        }
    }
}
