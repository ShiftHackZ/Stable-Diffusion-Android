package com.shifthackz.aisdv1.presentation.widget.usage

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.model.UsageCategory
import com.shifthackz.aisdv1.presentation.model.UsageItem
import com.shifthackz.aisdv1.presentation.model.UsageScreenKind
import com.shifthackz.aisdv1.presentation.model.UsageState
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.hypot

/**
 * Shared standalone layout for storage and network usage screens.
 *
 * The content owns the top app bar, shimmer loading state, interactive donut chart, category list,
 * and pinned primary CTA. It deliberately does not depend on app bottom navigation so callers can
 * mount it as a full-screen route like benchmark and configuration flows.
 *
 * @param modifier Compose modifier applied to the outer scaffold.
 * @param screen Storage or network flavor that controls copy, CTA, placeholders, and row actions.
 * @param usage Current dashboard data, including shimmer loading state and selected category.
 * @param onBack Called when the standalone screen back button is tapped.
 * @param onSelectCategory Called when a row or donut segment selects a usage category.
 * @param onClearCategory Called from storage rows when the user requests category deletion.
 * @param onPrimaryAction Called by the pinned CTA to clear storage or reset traffic statistics.
 *
 * @author Dmitriy Moroz
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsageScreenContent(
    modifier: Modifier = Modifier,
    screen: UsageScreenKind,
    usage: UsageState,
    onBack: () -> Unit = {},
    onSelectCategory: (UsageCategory) -> Unit = {},
    onClearCategory: (UsageCategory) -> Unit = {},
    onPrimaryAction: () -> Unit = {},
) {
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = Localization.string("action_back"),
                        )
                    }
                },
                title = {
                    Text(
                        text = Localization.string(screen.titleKey()),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        },
        bottomBar = {
            Button(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .height(68.dp)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp, top = 8.dp),
                enabled = !usage.loading && (usage.totalBytes > 0L || screen == UsageScreenKind.NETWORK),
                onClick = onPrimaryAction,
            ) {
                Text(
                    text = when (screen) {
                        UsageScreenKind.STORAGE -> Localization.string(
                            "settings_usage_clear_all_storage",
                            usage.totalBytes.formatBytes(),
                        )
                        UsageScreenKind.NETWORK -> Localization.string("settings_usage_reset_network")
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScrollbar(scrollState)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (usage.loading) {
                UsageLoadingContent(screen = screen)
            } else {
                UsageDonutChart(
                    usage = usage,
                    onSelect = onSelectCategory,
                )
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = Localization.string(screen.titleKey()),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = usage.subtitle(screen),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(22.dp))
                UsageCategoryList(
                    usage = usage,
                    showClearActions = screen == UsageScreenKind.STORAGE,
                    onSelectCategory = onSelectCategory,
                    onClearCategory = onClearCategory,
                )
                Text(
                    modifier = Modifier.padding(top = 14.dp, bottom = 24.dp),
                    text = Localization.string(screen.noteKey()),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun UsageLoadingContent(
    screen: UsageScreenKind,
) {
    UsageDonutChartPlaceholder()
    Spacer(modifier = Modifier.height(22.dp))
    Text(
        text = Localization.string(screen.titleKey()),
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
    ShimmerLine(
        modifier = Modifier
            .padding(top = 12.dp)
            .width(260.dp)
            .height(16.dp),
    )
    ShimmerLine(
        modifier = Modifier
            .padding(top = 8.dp)
            .width(190.dp)
            .height(16.dp),
    )
    Spacer(modifier = Modifier.height(22.dp))
    UsageCategoryListPlaceholder(rowCount = screen.placeholderRowsCount())
    ShimmerLine(
        modifier = Modifier
            .padding(top = 16.dp, bottom = 24.dp)
            .fillMaxWidth(0.82f)
            .height(14.dp),
    )
}

@Composable
private fun UsageDonutChartPlaceholder() {
    Box(
        modifier = Modifier.size(246.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .size(128.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                ShimmerLine(
                    modifier = Modifier
                        .width(78.dp)
                        .height(20.dp),
                )
                ShimmerLine(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(54.dp)
                        .height(12.dp),
                )
            }
        }
    }
}

@Composable
private fun UsageCategoryListPlaceholder(
    rowCount: Int,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.78f),
    ) {
        Column {
            repeat(rowCount) { index ->
                UsageCategoryPlaceholderRow(showDivider = index < rowCount - 1)
            }
        }
    }
}

@Composable
private fun UsageCategoryPlaceholderRow(
    showDivider: Boolean,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ShimmerLine(
                modifier = Modifier
                    .size(34.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                ShimmerLine(
                    modifier = Modifier
                        .fillMaxWidth(0.62f)
                        .height(18.dp),
                )
                ShimmerLine(
                    modifier = Modifier
                        .fillMaxWidth(0.38f)
                        .height(12.dp),
                )
            }
            ShimmerLine(
                modifier = Modifier
                    .width(74.dp)
                    .height(18.dp),
            )
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)),
            )
        }
    }
}

@Composable
private fun UsageDonutChart(
    usage: UsageState,
    onSelect: (UsageCategory) -> Unit,
) {
    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 720,
            easing = FastOutSlowInEasing,
        ),
        label = "usage-chart-progress",
    )
    val totalBytes = usage.totalBytes
    val segments = remember(usage.items, totalBytes) {
        usage.items.chartSegments(totalBytes)
    }
    val selectedItem = usage.selectedItem
    val centerValue = selectedItem?.bytes ?: totalBytes
    val centerLabel = selectedItem?.category?.title() ?: Localization.string("settings_usage_total")

    Box(
        modifier = Modifier.size(246.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(segments) {
                    detectTapGestures { offset ->
                        val category = hitCategory(
                            offset = offset,
                            segments = segments,
                            strokeWidthPx = 40.dp.toPx(),
                            canvasWidth = size.width.toFloat(),
                            canvasHeight = size.height.toFloat(),
                        )
                        category?.let(onSelect)
                    }
                },
        ) {
            val diameter = size.minDimension
            val baseStroke = 34.dp.toPx()
            val selectedStroke = 42.dp.toPx()
            val inset = selectedStroke / 2f + 2.dp.toPx()
            val arcSize = Size(diameter - inset * 2f, diameter - inset * 2f)
            val topLeft = Offset(
                x = (size.width - arcSize.width) / 2f,
                y = (size.height - arcSize.height) / 2f,
            )

            if (segments.isEmpty()) {
                drawCircle(
                    color = Color(0xFF697183).copy(alpha = 0.16f),
                    radius = arcSize.width / 2f,
                    center = center,
                    style = Stroke(width = baseStroke, cap = StrokeCap.Round),
                )
            }

            val visibleAngle = 360f * progress
            segments.forEach { segment ->
                val sweep = (
                    minOf(segment.startAngle + segment.sweepAngle, visibleAngle) -
                        segment.startAngle
                    ).coerceIn(0f, segment.sweepAngle)
                if (sweep <= 0f) return@forEach
                val item = segment.item
                val isSelected = item.category == usage.selectedCategory
                val color = item.category.color().copy(alpha = if (isSelected) 1f else 0.82f)
                val stroke = Stroke(
                    width = if (isSelected) selectedStroke else baseStroke,
                    cap = StrokeCap.Butt,
                )
                if (segments.size == 1 && sweep >= 359.9f) {
                    drawCircle(
                        color = color,
                        radius = arcSize.width / 2f,
                        center = center,
                        style = stroke,
                    )
                } else {
                    drawArc(
                        color = color,
                        startAngle = -90f + segment.startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = stroke,
                    )
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = centerValue.formatBytes(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = centerLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun UsageCategoryList(
    usage: UsageState,
    showClearActions: Boolean,
    onSelectCategory: (UsageCategory) -> Unit,
    onClearCategory: (UsageCategory) -> Unit,
) {
    val percentLabels = remember(usage.items, usage.totalBytes) {
        usage.items.percentLabels(usage.totalBytes)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.78f),
    ) {
        Column {
            usage.items.forEachIndexed { index, item ->
                UsageCategoryRow(
                    item = item,
                    selected = item.category == usage.selectedCategory,
                    percentLabel = percentLabels[item.category].orEmpty(),
                    showClearAction = showClearActions && item.enabled,
                    showDivider = index < usage.items.lastIndex,
                    onSelectCategory = onSelectCategory,
                    onClearCategory = onClearCategory,
                )
            }
        }
    }
}

@Composable
private fun UsageCategoryRow(
    item: UsageItem,
    selected: Boolean,
    percentLabel: String,
    showClearAction: Boolean,
    showDivider: Boolean,
    onSelectCategory: (UsageCategory) -> Unit,
    onClearCategory: (UsageCategory) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = item.enabled) {
                    onSelectCategory(item.category)
                }
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = item.category.color(),
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = MaterialTheme.colorScheme.onPrimary,
                                shape = CircleShape,
                            ),
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = item.category.title(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W600,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.enabled) {
                    Text(
                        text = percentLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                }
            }
            Text(
                modifier = Modifier.widthIn(min = 68.dp),
                text = if (item.enabled) {
                    item.bytes.formatBytes()
                } else {
                    Localization.string("settings_usage_empty")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
            )
            if (showClearAction) {
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        onClearCategory(item.category)
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = Localization.string(
                            "settings_usage_clear_category",
                            item.category.title(),
                        ),
                    )
                }
            }
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 60.dp)
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)),
            )
        }
    }
}

private fun hitCategory(
    offset: Offset,
    segments: List<UsageChartSegment>,
    strokeWidthPx: Float,
    canvasWidth: Float,
    canvasHeight: Float,
): UsageCategory? {
    if (segments.isEmpty()) return null

    val centerX = canvasWidth / 2f
    val centerY = canvasHeight / 2f
    val dx = offset.x - centerX
    val dy = offset.y - centerY
    val radius = minOf(canvasWidth, canvasHeight) / 2f
    val distance = hypot(dx, dy)
    val innerRadius = radius - strokeWidthPx - 16f
    val outerRadius = radius + 16f
    if (distance !in innerRadius..outerRadius) return null

    val angle = ((atan2(dy, dx) * 180f / PI.toFloat()) + 450f) % 360f
    segments.forEach { segment ->
        if (angle >= segment.startAngle && angle < segment.startAngle + segment.sweepAngle) {
            return segment.item.category
        }
    }
    return segments.lastOrNull()?.item?.category
}

@Composable
private fun ShimmerLine(
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .shimmer(),
    )
}

private fun UsageScreenKind.placeholderRowsCount(): Int = when (this) {
    UsageScreenKind.STORAGE -> 6
    UsageScreenKind.NETWORK -> 3
}

private data class UsageChartSegment(
    val item: UsageItem,
    val startAngle: Float,
    val sweepAngle: Float,
)

/**
 * Builds tappable donut segments that always fill exactly 360 degrees.
 *
 * Very small non-empty categories get a minimum visual sweep so users can still tap them, while the
 * largest segment absorbs floating-point correction to avoid gray gaps between arcs.
 *
 * @receiver Ordered usage rows rendered by the current dashboard.
 * @param totalBytes Sum of enabled category bytes used as the raw sweep denominator.
 *
 * @author Dmitriy Moroz
 */
private fun List<UsageItem>.chartSegments(
    totalBytes: Long,
): List<UsageChartSegment> {
    val enabledItems = filter(UsageItem::enabled)
    if (totalBytes <= 0L || enabledItems.isEmpty()) return emptyList()
    if (enabledItems.size == 1) {
        return listOf(
            UsageChartSegment(
                item = enabledItems.first(),
                startAngle = 0f,
                sweepAngle = 360f,
            ),
        )
    }

    val minSweep = MIN_CHART_SWEEP_DEGREES.coerceAtMost(360f / enabledItems.size * 0.6f)
    val rawSweeps = enabledItems.map { item ->
        item.bytes.toFloat() / totalBytes.toFloat() * 360f
    }
    val smallIndexes = rawSweeps
        .mapIndexedNotNull { index, sweep -> index.takeIf { sweep < minSweep } }
        .toSet()

    val sweeps = if (smallIndexes.size == enabledItems.size) {
        List(enabledItems.size) { 360f / enabledItems.size }
    } else {
        val reserved = smallIndexes.size * minSweep
        val remaining = (360f - reserved).coerceAtLeast(0f)
        val largeRawTotal = rawSweeps
            .filterIndexed { index, _ -> index !in smallIndexes }
            .sum()
            .takeIf { it > 0f }
            ?: 1f
        rawSweeps.mapIndexed { index, sweep ->
            if (index in smallIndexes) {
                minSweep
            } else {
                sweep / largeRawTotal * remaining
            }
        }
    }.toMutableList()

    val correction = 360f - sweeps.sum()
    val correctionIndex = sweeps.indices.maxByOrNull { sweeps[it] } ?: 0
    sweeps[correctionIndex] += correction

    var cursor = 0f
    return enabledItems.mapIndexed { index, item ->
        UsageChartSegment(
            item = item,
            startAngle = cursor,
            sweepAngle = sweeps[index],
        ).also {
            cursor += sweeps[index]
        }
    }
}

/**
 * Formats category percentages so visible rows always add up to 100%.
 *
 * Labels are calculated in tenths to keep tiny non-empty categories visible as at least 0.1% without
 * losing the leftover percent to rounding.
 *
 * @receiver Ordered usage rows rendered by the current dashboard.
 * @param totalBytes Sum of enabled category bytes used as the raw percentage denominator.
 *
 * @author Dmitriy Moroz
 */
private fun List<UsageItem>.percentLabels(totalBytes: Long): Map<UsageCategory, String> {
    val enabledItems = filter(UsageItem::enabled)
    if (totalBytes <= 0L || enabledItems.isEmpty()) return emptyMap()
    if (enabledItems.size == 1) {
        return mapOf(enabledItems.first().category to "100%")
    }

    val rawUnits = enabledItems.map { item ->
        item.bytes.toDouble() / totalBytes.toDouble() * PERCENT_TENTHS_TOTAL
    }
    val units = rawUnits
        .map { raw -> floor(raw).toInt().coerceAtLeast(MIN_PERCENT_TENTH) }
        .toMutableList()

    var delta = PERCENT_TENTHS_TOTAL - units.sum()
    if (delta > 0) {
        val order = rawUnits.indices
            .sortedByDescending { index -> rawUnits[index] - floor(rawUnits[index]) }
        var cursor = 0
        while (delta > 0 && order.isNotEmpty()) {
            units[order[cursor % order.size]] += 1
            cursor++
            delta--
        }
    } else if (delta < 0) {
        val order = units.indices.sortedByDescending { index -> units[index] - MIN_PERCENT_TENTH }
        var cursor = 0
        while (delta < 0 && order.isNotEmpty()) {
            val index = order[cursor % order.size]
            if (units[index] > MIN_PERCENT_TENTH) {
                units[index] -= 1
                delta++
            }
            cursor++
        }
    }

    return enabledItems
        .mapIndexed { index, item -> item.category to units[index].formatPercentUnits() }
        .toMap()
}

private fun Int.formatPercentUnits(): String {
    val whole = this / 10
    val decimal = this % 10
    return if (decimal == 0) {
        "$whole%"
    } else {
        "$whole.$decimal%"
    }
}

private fun UsageScreenKind.titleKey(): String = when (this) {
    UsageScreenKind.STORAGE -> "settings_usage_storage_title"
    UsageScreenKind.NETWORK -> "settings_usage_network_title"
}

private fun UsageScreenKind.subtitleKey(): String = when (this) {
    UsageScreenKind.STORAGE -> "settings_usage_storage_subtitle"
    UsageScreenKind.NETWORK -> "settings_usage_network_subtitle"
}

private fun UsageState.subtitle(screen: UsageScreenKind): String = when (screen) {
    UsageScreenKind.STORAGE -> storageSubtitle()
    UsageScreenKind.NETWORK -> Localization.string(
        screen.subtitleKey(),
        totalBytes.formatBytes(),
    )
}

private fun UsageState.storageSubtitle(): String {
    val names = items
        .filter(UsageItem::enabled)
        .map { item -> item.category.summaryTitle() }
    if (totalBytes <= 0L || names.isEmpty()) {
        return Localization.string("settings_usage_storage_subtitle_empty")
    }
    return Localization.string(
        "settings_usage_storage_subtitle",
        totalBytes.formatBytes(),
        names.joinLocalized(),
    )
}

private fun List<String>.joinLocalized(): String = when (size) {
    0 -> ""
    1 -> first()
    2 -> first() +
        Localization.string("settings_usage_list_last_separator") +
        last()

    else -> dropLast(1).joinToString(Localization.string("settings_usage_list_separator")) +
        Localization.string("settings_usage_list_last_separator") +
        last()
}

private fun UsageScreenKind.noteKey(): String = when (this) {
    UsageScreenKind.STORAGE -> "settings_usage_storage_note"
    UsageScreenKind.NETWORK -> "settings_usage_network_note"
}

/**
 * User-facing category title used in rows and clear confirmation dialogs.
 *
 * @author Dmitriy Moroz
 */
fun UsageCategory.title(): String = Localization.string(
    when (this) {
        UsageCategory.CACHE -> "settings_usage_category_cache"
        UsageCategory.GALLERY -> "settings_usage_category_gallery"
        UsageCategory.MODELS_ONNX -> "settings_usage_category_models_onnx"
        UsageCategory.MODELS_MEDIAPIPE -> "settings_usage_category_models_mediapipe"
        UsageCategory.MODELS_SDXL -> "settings_usage_category_models_sdxl"
        UsageCategory.MODELS_CORE_ML -> "settings_usage_category_models_core_ml"
        UsageCategory.TRAFFIC_MODELS -> "settings_usage_category_traffic_models"
        UsageCategory.TRAFFIC_CONFIGS -> "settings_usage_category_traffic_configs"
        UsageCategory.TRAFFIC_INFERENCE -> "settings_usage_category_traffic_inference"
    },
)

/**
 * Short noun phrase used when summarizing which storage categories currently occupy space.
 *
 * @author Dmitriy Moroz
 */
fun UsageCategory.summaryTitle(): String = Localization.string(
    when (this) {
        UsageCategory.CACHE -> "settings_usage_summary_cache"
        UsageCategory.GALLERY -> "settings_usage_summary_gallery"
        UsageCategory.MODELS_ONNX -> "settings_usage_summary_models_onnx"
        UsageCategory.MODELS_MEDIAPIPE -> "settings_usage_summary_models_mediapipe"
        UsageCategory.MODELS_SDXL -> "settings_usage_summary_models_sdxl"
        UsageCategory.MODELS_CORE_ML -> "settings_usage_summary_models_core_ml"
        UsageCategory.TRAFFIC_MODELS -> "settings_usage_category_traffic_models"
        UsageCategory.TRAFFIC_CONFIGS -> "settings_usage_category_traffic_configs"
        UsageCategory.TRAFFIC_INFERENCE -> "settings_usage_category_traffic_inference"
    },
)

private fun UsageCategory.color(): Color = when (this) {
    UsageCategory.CACHE -> Color(0xFF4C7EF3)
    UsageCategory.GALLERY -> Color(0xFF6EC6F2)
    UsageCategory.MODELS_ONNX -> Color(0xFFF59F32)
    UsageCategory.MODELS_MEDIAPIPE -> Color(0xFFF04462)
    UsageCategory.MODELS_SDXL -> Color(0xFF59C96B)
    UsageCategory.MODELS_CORE_ML -> Color(0xFF7E66E8)
    UsageCategory.TRAFFIC_MODELS -> Color(0xFF3D8BFF)
    UsageCategory.TRAFFIC_CONFIGS -> Color(0xFF65C96F)
    UsageCategory.TRAFFIC_INFERENCE -> Color(0xFFF0A13A)
}

private fun Long.formatBytes(): String {
    val value = coerceAtLeast(0L)
    val units = listOf("B", "KB", "MB", "GB", "TB")
    var unitIndex = 0
    var display = value.toDouble()
    while (display >= 1000.0 && unitIndex < units.lastIndex) {
        display /= 1000.0
        unitIndex++
    }

    return if (unitIndex == 0 || display >= 100.0) {
        "${display.toLong()} ${units[unitIndex]}"
    } else {
        "${(display * 10.0).toInt() / 10.0} ${units[unitIndex]}"
    }
}

private const val MIN_CHART_SWEEP_DEGREES = 6f
private const val MIN_PERCENT_TENTH = 1
private const val PERCENT_TENTHS_TOTAL = 1000
