@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.download.DownloadDialog
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialogContent
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.input.PlatformOutlinedTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlinx.coroutines.launch


/**
 * Renders the `ServerSetupLoading` UI for the SDAI presentation layer.
 *
 * @param text text value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ServerSetupLoading(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(56.dp))
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = text,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
/**
 * Renders the `ConfigurationStepBar` UI for the SDAI presentation layer.
 *
 * @param currentStep current step value consumed by the API.
 * @param strings strings value consumed by the API.
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
/**
 * Renders the `SourceSelectionStep` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param strings strings value consumed by the API.
 * @param listState list state value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SourceSelectionStep(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    listState: LazyListState,
    processIntent: (ServerSetupIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = state.allowedModes.indexOf(state.mode)
    LaunchedEffect(selectedIndex, state.allowedModes.size) {
        if (selectedIndex >= 0) {
            if (!listState.isItemFullyVisible(selectedIndex)) {
                listState.animateScrollToItem(selectedIndex)
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.verticalScrollbar(listState),
        state = listState,
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = state.allowedModes,
            key = { _, source -> source.key },
        ) { index, source ->
            SourceModeItem(
                source = source,
                selected = state.mode == source,
                strings = strings,
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index)
                    }
                    processIntent(ServerSetupIntent.UpdateServerMode(source))
                },
            )
        }
    }
}

/**
 * Executes the `isItemFullyVisible` step in the SDAI presentation layer.
 *
 * @param index index value consumed by the API.
 * @return Result produced by `isItemFullyVisible`.
 * @author Dmitriy Moroz
 */
internal fun LazyListState.isItemFullyVisible(index: Int): Boolean {
    val item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return false
    return item.offset >= layoutInfo.viewportStartOffset &&
        item.offset + item.size <= layoutInfo.viewportEndOffset
}
/**
 * Renders the `SourceModeItem` UI for the SDAI presentation layer.
 *
 * @param source source value consumed by the API.
 * @param selected selected value consumed by the API.
 * @param strings strings value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SourceModeItem(
    source: ServerSource,
    selected: Boolean,
    strings: ServerSetupStrings,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            Row {
                Icon(
                    modifier = Modifier
                        .size(42.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
                    imageVector = source.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 8.dp),
                    text = source.title(strings),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = source.subtitle(strings),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                modifier = Modifier.padding(4.dp),
            ) {
                source.featureTags.forEach { tag ->
                    Text(
                        modifier = Modifier
                            .padding(4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceTint,
                                shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        text = tag.mapToUi(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.W300,
                    )
                }
            }
        }
    }
}
