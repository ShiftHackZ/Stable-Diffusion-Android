@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.benchmark

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkAccelerationStatus
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkAccelerator
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkDeviceInfo
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkPlatform
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkProviderIssue
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkProviderRecommendation
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkResult
import com.shifthackz.aisdv1.feature.benchmark.accelerationCapabilities
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.BenchmarkRouter
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import org.koin.core.parameter.parametersOf

/**
 * Renders the benchmark screen with its Koin-provided ViewModel.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param router navigation router used by the screen.
 * @author Dmitriy Moroz
 */
@Composable
fun BenchmarkScreen(
    modifier: Modifier = Modifier,
    router: BenchmarkRouter? = null,
) {
    val koin = remember { initKoin() }
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<BenchmarkRouter>()
    }
    val viewModel = remember(koin, resolvedRouter) {
        koin.get<BenchmarkViewModel> {
            parametersOf(resolvedRouter)
        }
    }
    MviComponent(viewModel = viewModel) { state, intentHandler ->
        BenchmarkScreenContent(
            modifier = modifier,
            state = state,
            processIntent = intentHandler,
        )
    }
}

/**
 * Renders benchmark screen content.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent callback for benchmark actions.
 * @author Dmitriy Moroz
 */
@Composable
fun BenchmarkScreenContent(
    modifier: Modifier = Modifier,
    state: BenchmarkState,
    processIntent: (BenchmarkIntent) -> Unit = {},
) {
    val listState = rememberLazyListState()
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { processIntent(BenchmarkIntent.NavigateBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = Localization.string("action_back"),
                        )
                    }
                },
                title = {
                    Text(
                        text = Localization.string("title_benchmark"),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                actions = {
                    IconButton(
                        enabled = state.latestResult != null && !state.running,
                        onClick = { processIntent(BenchmarkIntent.ShareResults) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = Localization.string("benchmark_share_results"),
                        )
                    }
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
                enabled = !state.running,
                onClick = { processIntent(BenchmarkIntent.RunBenchmark) },
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = if (state.running) {
                        Localization.string("benchmark_running")
                    } else {
                        Localization.string("benchmark_run")
                    },
                )
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScrollbar(listState),
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                    bottom = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    ScoreSection(
                        result = state.latestResult,
                        loading = state.loadingResult,
                        running = state.running,
                    )
                }
                item {
                    DeviceSection(
                        loading = state.loadingDevice,
                        deviceInfo = state.deviceInfo ?: state.latestResult?.deviceInfo,
                    )
                }
                item {
                    CapabilitiesSection(
                        loading = state.loadingDevice,
                        deviceInfo = state.deviceInfo ?: state.latestResult?.deviceInfo,
                    )
                }
                item {
                    ProviderRecommendationsSection(
                        loading = state.loadingResult,
                        deviceInfo = state.deviceInfo ?: state.latestResult?.deviceInfo,
                        recommendations = state.latestResult?.providerRecommendations.orEmpty(),
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                visible = state.error != null,
            ) {
                state.error?.let { error ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        tonalElevation = 6.dp,
                        color = MaterialTheme.colorScheme.errorContainer,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            OutlinedButton(
                                onClick = { processIntent(BenchmarkIntent.DismissError) },
                            ) {
                                Text(Localization.string("ok"))
                            }
                        }
                    }
                }
            }
        }
    }
    if (state.running) {
        ProgressDialog(
            title = Localization.string("benchmark_running_title").asUiText(),
            subTitle = Localization.string("benchmark_running_sub_title").asUiText(),
            canDismiss = false,
        )
    }
}

@Composable
private fun ScoreSection(
    result: BenchmarkResult?,
    loading: Boolean,
    running: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Localization.string("benchmark_score"),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    if (loading && result == null) {
                        ShimmerLine(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .width(96.dp)
                                .height(36.dp),
                        )
                    } else {
                        Text(
                            text = result?.totalScore?.toString()
                                ?: Localization.string("benchmark_no_results"),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            if (loading && result == null) {
                ScoreInfoRowSkeleton()
                ScoreInfoRowSkeleton()
            } else if (result != null) {
                ScoreInfoRow(
                    label = Localization.string("benchmark_estimated_time_short"),
                    value = Localization.string("benchmark_seconds", result.estimatedTimeSeconds),
                )
                ScoreInfoRow(
                    label = Localization.string("benchmark_last_run"),
                    value = formatBenchmarkTimestamp(result.createdAt),
                )
            }
            if (running) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun ScoreInfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            modifier = Modifier.weight(0.42f),
            text = label,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier.weight(0.58f),
            text = value,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DeviceSection(
    loading: Boolean,
    deviceInfo: BenchmarkDeviceInfo?,
) {
    SectionCard(
        icon = Icons.Default.Memory,
        title = Localization.string("benchmark_hardware"),
    ) {
        if (loading && deviceInfo == null) {
            TableRowsSkeleton(count = 7)
        } else {
            val info = deviceInfo
            InfoRow(Localization.string("benchmark_device"), info?.deviceName().orUnknown())
            InfoRow(Localization.string("benchmark_os"), info?.osVersion.orUnknown())
            InfoRow(Localization.string("benchmark_cpu"), info?.cpuName.orUnknown())
            InfoRow(
                Localization.string("benchmark_processors"),
                info?.cpuCores?.toString().orUnknown(),
            )
            InfoRow(Localization.string("benchmark_gpu"), info?.gpuName.orUnknown())
            InfoRow(
                Localization.string("benchmark_ram"),
                info?.totalRamMb?.let { Localization.string("benchmark_mb", it) }.orUnknown(),
            )
            InfoRow(
                Localization.string("benchmark_vram"),
                info?.totalVramMb?.let { Localization.string("benchmark_mb", it) }
                    ?: Localization.string("benchmark_unknown"),
            )
        }
    }
}

@Composable
private fun CapabilitiesSection(
    loading: Boolean,
    deviceInfo: BenchmarkDeviceInfo?,
) {
    SectionCard(
        icon = Icons.Default.DeveloperBoard,
        title = Localization.string("benchmark_capabilities"),
    ) {
        if (loading && deviceInfo == null) {
            TableRowsSkeleton(count = 6)
        } else {
            val capabilities = deviceInfo?.accelerationCapabilities().orEmpty()
            BenchmarkAccelerator.entries.forEach { accelerator ->
                val capability = capabilities.firstOrNull { it.accelerator == accelerator }
                InfoRowContent(label = accelerator.displayName()) {
                    AccelerationStatusChip(status = capability?.status)
                }
            }
        }
    }
}

@Composable
private fun ProviderRecommendationsSection(
    loading: Boolean,
    deviceInfo: BenchmarkDeviceInfo?,
    recommendations: List<BenchmarkProviderRecommendation>,
) {
    val providers = recommendations.ifEmpty {
        deviceInfo?.localBenchmarkProviders().orEmpty().map { provider ->
            BenchmarkProviderRecommendation(
                provider = provider,
                recommended = false,
                width = 0,
                height = 0,
                samplingSteps = 0,
                cfgScale = 0f,
                batchCount = 1,
                estimatedTimeSeconds = 0,
                backgroundGeneration = false,
                sdxlBackend = SdxlBackend.CPU,
            )
        }
    }
    SectionCard(
        icon = Icons.Default.Tune,
        title = Localization.string("benchmark_recommendations"),
    ) {
        if (loading && recommendations.isEmpty()) {
            ProviderRecommendationsSkeleton()
        } else if (providers.isEmpty()) {
            Text(
                text = Localization.string("benchmark_recommendations_empty"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                providers.forEach { recommendation ->
                    ProviderRecommendationBlock(
                        recommendation = recommendation,
                        pending = recommendations.isEmpty(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderRecommendationBlock(
    recommendation: BenchmarkProviderRecommendation,
    pending: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ProviderRecommendationTitle(
            text = Localization.string(
                "benchmark_provider_recommended_settings",
                recommendation.provider.displayName(),
            ),
        )
        when {
            pending -> ProviderRecommendationNote(
                text = Localization.string("benchmark_recommendations_empty"),
            )
            !recommendation.recommended -> {
                InfoRow(
                    Localization.string("benchmark_status"),
                    Localization.string("benchmark_provider_not_recommended"),
                )
                recommendation.issues.ifEmpty {
                    listOf(BenchmarkProviderIssue.LOW_SCORE)
                }.forEach { issue ->
                    ProviderRecommendationNote(text = issue.localizedText())
                }
            }
            else -> {
                InfoRow(
                    Localization.string("benchmark_recommended_size"),
                    "${recommendation.width} x ${recommendation.height}",
                )
                InfoRow(
                    Localization.string("benchmark_recommended_steps"),
                    recommendation.samplingSteps.toString(),
                )
                InfoRow(
                    Localization.string("benchmark_recommended_cfg"),
                    recommendation.cfgScale.toString(),
                )
                InfoRow(
                    Localization.string("benchmark_estimated_time_short"),
                    Localization.string("benchmark_seconds", recommendation.estimatedTimeSeconds),
                )
                InfoRow(
                    Localization.string("benchmark_recommended_background"),
                    if (recommendation.backgroundGeneration) {
                        Localization.string("benchmark_recommended")
                    } else {
                        Localization.string("benchmark_not_recommended")
                    },
                )
                if (recommendation.provider == ServerSource.LOCAL_STABLE_DIFFUSION_CPP) {
                    InfoRow(
                        Localization.string("benchmark_recommended_backend"),
                        recommendation.sdxlBackend.displayName(),
                    )
                }
                recommendation.issues.forEach { issue ->
                    ProviderRecommendationNote(text = issue.localizedText())
                }
            }
        }
    }
}

@Composable
private fun ProviderRecommendationTitle(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ProviderRecommendationNote(
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            content()
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
) {
    InfoRowContent(label = label) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun InfoRowContent(
    label: String,
    value: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            modifier = Modifier.weight(0.42f),
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier.weight(0.58f),
        ) {
            value()
        }
    }
}

@Composable
private fun AccelerationStatusChip(status: BenchmarkAccelerationStatus?) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(status.containerColor())
            .padding(horizontal = 6.dp, vertical = 2.dp),
        text = status.localizedText(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.W500,
        color = status.contentColor(),
    )
}

@Composable
private fun ScoreInfoRowSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerLine(
            modifier = Modifier
                .weight(0.42f)
                .height(16.dp),
        )
        ShimmerLine(
            modifier = Modifier
                .weight(0.58f)
                .height(16.dp),
        )
    }
}

@Composable
private fun TableRowsSkeleton(count: Int) {
    repeat(count) { index ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerLine(
                modifier = Modifier
                    .weight(0.42f)
                    .height(16.dp),
            )
            ShimmerLine(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxWidth(if (index % 2 == 0) 0.86f else 0.68f)
                    .height(16.dp),
            )
        }
    }
}

@Composable
private fun ProviderRecommendationsSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(3) { blockIndex ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerLine(
                    modifier = Modifier
                        .fillMaxWidth(if (blockIndex == 0) 0.62f else 0.74f)
                        .height(18.dp),
                )
                repeat(if (blockIndex == 0) 4 else 2) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ShimmerLine(
                            modifier = Modifier
                                .weight(0.42f)
                                .height(16.dp),
                        )
                        ShimmerLine(
                            modifier = Modifier
                                .weight(0.58f)
                                .fillMaxWidth(if (index % 2 == 0) 0.55f else 0.72f)
                                .height(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShimmerLine(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .shimmer(),
    )
}

private fun BenchmarkDeviceInfo.deviceName(): String =
    listOf(manufacturer, model)
        .filter(String::isNotBlank)
        .joinToString(" ")
        .ifBlank { Localization.string("benchmark_unknown") }

private fun BenchmarkDeviceInfo.localBenchmarkProviders(): List<ServerSource> = when (platform) {
    BenchmarkPlatform.ANDROID -> listOf(
        ServerSource.LOCAL_MICROSOFT_ONNX,
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
        ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
    )
    BenchmarkPlatform.IOS -> listOf(ServerSource.LOCAL_APPLE_CORE_ML)
    BenchmarkPlatform.UNKNOWN -> emptyList()
}

private fun BenchmarkAccelerator.displayName(): String = when (this) {
    BenchmarkAccelerator.VULKAN -> "Vulkan backend"
    BenchmarkAccelerator.OPEN_CL -> "OpenCL backend"
    BenchmarkAccelerator.NNAPI -> "NNAPI delegate"
    BenchmarkAccelerator.METAL -> "Metal"
    BenchmarkAccelerator.CORE_ML -> "Core ML"
    BenchmarkAccelerator.NEURAL_ENGINE -> "Neural Engine"
}

private fun BenchmarkAccelerationStatus?.localizedText(): String = when (this) {
    BenchmarkAccelerationStatus.SUPPORTED -> Localization.string("benchmark_backend_supported")
    BenchmarkAccelerationStatus.BACKEND_UNAVAILABLE -> Localization.string("benchmark_backend_unavailable")
    BenchmarkAccelerationStatus.NOT_VALIDATED -> Localization.string("benchmark_backend_not_validated")
    BenchmarkAccelerationStatus.NOT_RECOMMENDED -> Localization.string("benchmark_backend_not_recommended")
    BenchmarkAccelerationStatus.UNAVAILABLE,
    null -> Localization.string("benchmark_unavailable")
}

private fun BenchmarkAccelerationStatus?.containerColor(): Color = when (this) {
    BenchmarkAccelerationStatus.SUPPORTED -> Color(0xFF388E3C)
    BenchmarkAccelerationStatus.NOT_RECOMMENDED -> Color(0xFFFFD54F)
    BenchmarkAccelerationStatus.NOT_VALIDATED -> Color(0xFFF57C00)
    BenchmarkAccelerationStatus.BACKEND_UNAVAILABLE,
    BenchmarkAccelerationStatus.UNAVAILABLE,
    null -> Color(0xFFD32F2F)
}

private fun BenchmarkAccelerationStatus?.contentColor(): Color = when (this) {
    BenchmarkAccelerationStatus.NOT_RECOMMENDED -> Color(0xFF3B2F00)
    else -> Color.White
}

private fun ServerSource.displayName(): String = when (this) {
    ServerSource.AUTOMATIC1111 -> Localization.string("srv_type_own_short")
    ServerSource.SWARM_UI -> Localization.string("srv_type_swarm_ui")
    ServerSource.LOCAL_MICROSOFT_ONNX -> Localization.string("srv_type_local_short")
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Localization.string("srv_type_media_pipe_short")
    ServerSource.LOCAL_STABLE_DIFFUSION_CPP -> Localization.string("srv_type_sdxl_short")
    ServerSource.LOCAL_APPLE_CORE_ML -> "Core ML"
    ServerSource.HORDE -> Localization.string("srv_type_horde_short")
    ServerSource.HUGGING_FACE -> Localization.string("srv_type_hugging_face_short")
    ServerSource.OPEN_AI -> Localization.string("srv_type_open_ai")
    ServerSource.STABILITY_AI -> Localization.string("srv_type_stability_ai")
    ServerSource.FAL_AI -> Localization.string("srv_type_fal_ai")
    ServerSource.ARLI_AI -> Localization.string("srv_type_arli_ai")
}

private fun SdxlBackend.displayName(): String = displayName

private fun BenchmarkProviderIssue.localizedText(): String = when (this) {
    BenchmarkProviderIssue.PLATFORM_UNSUPPORTED ->
        Localization.string("benchmark_issue_platform_unsupported")
    BenchmarkProviderIssue.LOW_MEMORY ->
        Localization.string("benchmark_issue_low_memory")
    BenchmarkProviderIssue.LOW_SCORE ->
        Localization.string("benchmark_issue_low_score")
    BenchmarkProviderIssue.ONNX_SLOW_LOW_MEMORY ->
        Localization.string("benchmark_issue_onnx_slow_low_memory")
    BenchmarkProviderIssue.MEDIAPIPE_UNSTABLE_LOW_MEMORY ->
        Localization.string("benchmark_issue_mediapipe_unstable_low_memory")
    BenchmarkProviderIssue.SDXL_TINY_EXPERIMENTAL_ONLY ->
        Localization.string("benchmark_issue_sdxl_tiny_experimental_only")
    BenchmarkProviderIssue.SDXL_BACKEND_NOT_VALIDATED ->
        Localization.string("benchmark_issue_sdxl_backend_not_validated")
    BenchmarkProviderIssue.ACCELERATOR_API_NOT_AVAILABLE ->
        Localization.string("benchmark_issue_accelerator_api_not_available")
}

private fun String?.orUnknown(): String = this ?: Localization.string("benchmark_unknown")
