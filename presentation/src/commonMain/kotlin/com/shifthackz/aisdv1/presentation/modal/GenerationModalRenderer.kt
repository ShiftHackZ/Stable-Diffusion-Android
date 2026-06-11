@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.model.GenerationModal
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
    }
}
