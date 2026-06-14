@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.feature.benchmark.BenchmarkLimitExceeded
import com.shifthackz.aisdv1.presentation.model.GenerationModal
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageBatchResultModal
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.InfoDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialogCancelButton

/**
 * Renders the `GenerationModalRenderer` UI for the SDAI presentation layer.
 *
 * @param screenModal screen modal value consumed by the API.
 * @param onDismissRequest callback invoked by the component.
 * @param onCancelGeneration callback invoked by the component.
 * @param onSaveRequest callback invoked by the component.
 * @param onReportRequest callback invoked by the component.
 * @param onViewDetailRequest callback invoked by the component.
 * @param onBenchmarkRequest callback invoked when the user wants to open benchmark.
 * @param onSkipBenchmarkRequest callback invoked when the user skips first-run benchmark.
 * @param onBenchmarkContinueRequest callback invoked when the user accepts recommendation risk.
 * @param onBenchmarkDoNotAskRequest callback invoked when the user suppresses future recommendation warnings.
 * @author Dmitriy Moroz
 */
@Composable
fun GenerationModalRenderer(
    screenModal: GenerationModal,
    onDismissRequest: () -> Unit,
    onCancelGeneration: () -> Unit,
    onSaveRequest: (List<AiGenerationResult>) -> Unit,
    onReportRequest: (AiGenerationResult) -> Unit,
    onViewDetailRequest: (AiGenerationResult) -> Unit,
    onBenchmarkRequest: () -> Unit,
    onSkipBenchmarkRequest: () -> Unit,
    onBenchmarkContinueRequest: () -> Unit,
    onBenchmarkDoNotAskRequest: () -> Unit,
) {
    when (screenModal) {
        GenerationModal.None -> Unit

        is GenerationModal.Communicating -> ProgressDialog(
            canDismiss = false,
            waitTimeSeconds = screenModal.hordeProcessStatus?.waitTimeSeconds,
            positionInQueue = screenModal.hordeProcessStatus?.queuePosition,
            content = screenModal.canCancel.takeIf { it }?.let {
                {
                    ProgressDialogCancelButton(onClick = onCancelGeneration)
                }
            },
        )

        is GenerationModal.Generating -> ProgressDialog(
            title = screenModal.title ?: Localization.string("communicating_local_title").asUiText(),
            canDismiss = false,
            step = screenModal.pair,
            content = screenModal.canCancel.takeIf { it }?.let {
                {
                    ProgressDialogCancelButton(onClick = onCancelGeneration)
                }
            },
        )

        is GenerationModal.Error -> ErrorDialog(
            text = screenModal.error,
            onDismissRequest = onDismissRequest,
        )

        is GenerationModal.Image.Single -> GenerationImageResultDialog(
            imageBase64 = screenModal.result.image,
            showSaveButton = !screenModal.autoSaveEnabled,
            showReportButton = screenModal.reportEnabled,
            onDismissRequest = onDismissRequest,
            onSaveRequest = { onSaveRequest(listOf(screenModal.result)) },
            onReportRequest = { onReportRequest(screenModal.result) },
            onViewDetailRequest = { onViewDetailRequest(screenModal.result) },
        )

        is GenerationModal.Image.Batch -> ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            GenerationImageBatchResultModal(
                results = screenModal.results,
                showSaveButton = !screenModal.autoSaveEnabled,
                onSaveRequest = { onSaveRequest(screenModal.results) },
                onViewDetailRequest = onViewDetailRequest,
            )
        }

        GenerationModal.Background.Running -> InfoDialog(
            title = Localization.string("interaction_background_running_title").asUiText(),
            subTitle = Localization.string("interaction_background_running_sub_title").asUiText(),
            onDismissRequest = onDismissRequest,
        )

        GenerationModal.Background.Scheduled -> InfoDialog(
            title = Localization.string("interaction_background_scheduled_title").asUiText(),
            subTitle = Localization.string("interaction_background_scheduled_sub_title").asUiText(),
            onDismissRequest = onDismissRequest,
        )

        GenerationModal.Benchmark.FirstLocalGeneration -> DecisionInteractiveDialog(
            title = Localization.string("benchmark_first_local_generation_title").asUiText(),
            text = Localization.string("benchmark_first_local_generation_text").asUiText(),
            confirmActionText = Localization.string("yes").asUiText(),
            dismissActionText = Localization.string("no").asUiText(),
            onConfirmAction = onBenchmarkRequest,
            onDismissRequest = onSkipBenchmarkRequest,
        )

        is GenerationModal.Benchmark.ExceedsRecommendation -> AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onBenchmarkContinueRequest) {
                    Text(Localization.string("yes"))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(Localization.string("no"))
                }
                TextButton(onClick = onBenchmarkDoNotAskRequest) {
                    Text(Localization.string("benchmark_exceeded_do_not_ask"))
                }
            },
            title = {
                Text(Localization.string("benchmark_exceeded_title"))
            },
            text = {
                Text(
                    text = screenModal.reasons.joinToString(
                        separator = "\n",
                        prefix = Localization.string("benchmark_exceeded_text") + "\n\n",
                    ) { reason -> reason.localizedText() },
                )
            },
        )
    }
}

private fun BenchmarkLimitExceeded.localizedText(): String = when (this) {
    BenchmarkLimitExceeded.IMAGE_SIZE -> Localization.string("benchmark_limit_image_size")
    BenchmarkLimitExceeded.SAMPLING_STEPS -> Localization.string("benchmark_limit_sampling_steps")
    BenchmarkLimitExceeded.BATCH_COUNT -> Localization.string("benchmark_limit_batch_count")
    BenchmarkLimitExceeded.HIRES_FIX -> Localization.string("benchmark_limit_hires_fix")
    BenchmarkLimitExceeded.PROVIDER -> Localization.string("benchmark_limit_provider")
    BenchmarkLimitExceeded.BACKEND -> Localization.string("benchmark_limit_backend")
}
