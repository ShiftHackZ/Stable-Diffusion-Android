@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.common.extensions.openAppSettings
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.ImageToImageIntent
import com.shifthackz.aisdv1.presentation.modal.crop.CropImageModal
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasScreen
import com.shifthackz.aisdv1.presentation.modal.grid.GridBottomSheet
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.modal.language.LanguageBottomSheet
import com.shifthackz.aisdv1.presentation.modal.ldscheduler.LDSchedulerBottomSheet
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagDialog
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuIntent
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailIntent
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryIntent
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintIntent
import com.shifthackz.aisdv1.presentation.screen.report.ReportIntent
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageBatchResultModal
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.InfoDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialogCancelButton
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.android.core.mvi.MviIntent
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ModalRenderer(
    screenModal: Modal,
    processIntent: (MviIntent) -> Unit,
) {
    val dismiss: () -> Unit = {
        processIntent(ServerSetupIntent.DismissDialog)
        processIntent(SettingsIntent.DismissDialog)
        processIntent(GenerationMviIntent.SetModal(Modal.None))
        processIntent(GalleryIntent.DismissDialog)
        processIntent(GalleryDetailIntent.DismissDialog)
        processIntent(InPaintIntent.ScreenModal.Dismiss)
        processIntent(DebugMenuIntent.DismissModal)
        processIntent(ReportIntent.DismissError)
    }
    val context = LocalContext.current
    when (screenModal) {
        Modal.None -> Unit

        is Modal.Communicating -> ProgressDialog(
            canDismiss = false,
            waitTimeSeconds = screenModal.hordeProcessStatus?.waitTimeSeconds,
            positionInQueue = screenModal.hordeProcessStatus?.queuePosition,
            content = screenModal.canCancel.takeIf { it }?.let {
                {
                    ProgressDialogCancelButton {
                        processIntent(GenerationMviIntent.Cancel.Generation)
                    }
                }
            },
        )

        Modal.LoadingRandomImage -> ProgressDialog(
            titleResId = LocalizationR.string.communicating_random_image_title,
            canDismiss = false,
        ) {
            ProgressDialogCancelButton {
                processIntent(GenerationMviIntent.Cancel.FetchRandomImage)
            }
        }

        is Modal.Error -> ErrorDialog(
            text = screenModal.error,
            onDismissRequest = dismiss,
        )

        is Modal.Generating -> ProgressDialog(
            titleResId = LocalizationR.string.communicating_local_title,
            canDismiss = false,
            step = screenModal.pair,
            content = screenModal.canCancel.takeIf { it }?.let {
                {
                    ProgressDialogCancelButton {
                        processIntent(GenerationMviIntent.Cancel.Generation)
                    }
                }
            },
        )

        is Modal.Image.Single -> GenerationImageResultDialog(
            imageBase64 = screenModal.result.image,
            showSaveButton = !screenModal.autoSaveEnabled,
            onDismissRequest = dismiss,
            onSaveRequest = {
                processIntent(GenerationMviIntent.Result.Save(listOf(screenModal.result)))
            },
            onReportRequest = {
                processIntent(GenerationMviIntent.Result.Report(screenModal.result))
            },
            onViewDetailRequest = {
                processIntent(GenerationMviIntent.Result.View(screenModal.result))
            },
        )

        is Modal.Image.Batch -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            GenerationImageBatchResultModal(
                screenModal.results,
                showSaveButton = !screenModal.autoSaveEnabled,
                onSaveRequest = {
                    processIntent(GenerationMviIntent.Result.Save(screenModal.results))
                },
                onViewDetailRequest = {
                    processIntent(GenerationMviIntent.Result.View(it))
                },
            )
        }

        is Modal.PromptBottomSheet -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            InputHistoryScreen(
                onGenerationSelected = { ai ->
                    val payload = when (screenModal.source) {
                        AiGenerationResult.Type.TEXT_TO_IMAGE -> {
                            GenerationFormUpdateEvent.Payload.T2IForm(ai)
                        }
                        AiGenerationResult.Type.IMAGE_TO_IMAGE -> {
                            GenerationFormUpdateEvent.Payload.I2IForm(ai, false)
                        }
                    }
                    processIntent(GenerationMviIntent.UpdateFromGeneration(payload))
                    processIntent(GenerationMviIntent.SetModal(Modal.None))
                },
            )
        }

        is Modal.ExtraBottomSheet -> ExtrasScreen(
            prompt = screenModal.prompt,
            negativePrompt = screenModal.negativePrompt,
            type = screenModal.type,
            onNewPrompts = { p, n ->
                processIntent(GenerationMviIntent.NewPrompts(p, n))
            },
            onClose = dismiss,
        )

        is Modal.Embeddings -> EmbeddingScreen(
            prompt = screenModal.prompt,
            negativePrompt = screenModal.negativePrompt,
            onNewPrompts = { p, n ->
                processIntent(GenerationMviIntent.NewPrompts(p, n))
            },
            onClose = dismiss,
        )

        is Modal.ClearAppCache -> DecisionInteractiveDialog(
            title = LocalizationR.string.title_clear_app_cache.asUiText(),
            text = LocalizationR.string.interaction_cache_sub_title.asUiText(),
            confirmActionResId = LocalizationR.string.yes,
            dismissActionResId = LocalizationR.string.no,
            onDismissRequest = dismiss,
            onConfirmAction = { processIntent(SettingsIntent.Action.ClearAppCache.Confirm) },
        )

        is Modal.SelectSdModel -> {
            var selectedItem by remember { mutableStateOf(screenModal.selected) }
            DecisionInteractiveDialog(
                title = LocalizationR.string.title_select_sd_model.asUiText(),
                text = UiText.empty,
                confirmActionResId = LocalizationR.string.action_select,
                onConfirmAction = { processIntent(SettingsIntent.SdModel.Select(selectedItem)) },
                onDismissRequest = dismiss,
                content = {
                    DropdownTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = LocalizationR.string.hint_sd_model.asUiText(),
                        value = selectedItem,
                        items = screenModal.models,
                        onItemSelected = { selectedItem = it },
                    )
                },
            )
        }

        is Modal.DeleteImageConfirm -> DecisionInteractiveDialog(
            title = when {
                screenModal.isAll -> LocalizationR.string.interaction_delete_all_title
                screenModal.isMultiple ->  LocalizationR.string.interaction_delete_selection_title
                else -> LocalizationR.string.interaction_delete_generation_title
            }.asUiText(),
            text = when {
                screenModal.isAll -> LocalizationR.string.interaction_delete_all_sub_title
                screenModal.isMultiple ->  LocalizationR.string.interaction_delete_selection_sub_title
                else -> LocalizationR.string.interaction_delete_generation_sub_title
            }.asUiText(),
            confirmActionResId = LocalizationR.string.yes,
            dismissActionResId = LocalizationR.string.no,
            onConfirmAction = {
                val intent = if (screenModal.isAll) {
                    GalleryIntent.Delete.All.Confirm
                } else if (screenModal.isMultiple) {
                    GalleryIntent.Delete.Selection.Confirm
                } else {
                    GalleryDetailIntent.Delete.Confirm
                }
                processIntent(intent)
            },
            onDismissRequest = dismiss,
        )

        is Modal.ConfirmExport -> DecisionInteractiveDialog(
            title = LocalizationR.string.interaction_export_title.asUiText(),
            text = if (screenModal.exportAll) {
                LocalizationR.string.interaction_export_sub_title
            } else {
                LocalizationR.string.interaction_export_sub_title_selection
            }.asUiText(),
            confirmActionResId = LocalizationR.string.action_export,
            onConfirmAction = {
                val intent = if (screenModal.exportAll) {
                    GalleryIntent.Export.All.Confirm
                } else {
                    GalleryIntent.Export.Selection.Confirm
                }
                processIntent(intent)
            },
            onDismissRequest = dismiss,
        )

        Modal.ExportInProgress -> ProgressDialog(
            titleResId = LocalizationR.string.exporting_progress_title,
            subTitleResId = LocalizationR.string.exporting_progress_sub_title,
            canDismiss = false,
        )

        is Modal.EditTag -> EditTagDialog(
            prompt = screenModal.prompt,
            negativePrompt = screenModal.negativePrompt,
            tag = screenModal.tag,
            isNegative = screenModal.isNegative,
            onDismissRequest = dismiss,
            onNewPrompts = { p, n ->
                processIntent(GenerationMviIntent.NewPrompts(p, n))
            },
        )

        Modal.Language -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            LanguageBottomSheet(onDismissRequest = dismiss)
        }

        is Modal.DeleteLocalModelConfirm -> DecisionInteractiveDialog(
            title = LocalizationR.string.interaction_delete_local_model_title.asUiText(),
            text = UiText.Resource(
                LocalizationR.string.interaction_delete_local_model_sub_title,
                screenModal.model.name,
            ),
            confirmActionResId = LocalizationR.string.yes,
            dismissActionResId = LocalizationR.string.no,
            onConfirmAction = { processIntent(ServerSetupIntent.LocalModel.DeleteConfirm(screenModal.model)) },
            onDismissRequest = dismiss,
        )

        Modal.ClearInPaintConfirm -> DecisionInteractiveDialog(
            title = LocalizationR.string.interaction_in_paint_clear_title.asUiText(),
            text = LocalizationR.string.interaction_in_paint_clear_title.asUiText(),
            confirmActionResId = LocalizationR.string.yes,
            dismissActionResId = LocalizationR.string.no,
            onConfirmAction = { processIntent(InPaintIntent.Action.Clear) },
            onDismissRequest = dismiss,
        )

        is Modal.Image.Crop -> CropImageModal(
            bitmap = screenModal.bitmap,
            onDismissRequest = dismiss,
            onResult = { processIntent(ImageToImageIntent.UpdateImage(it)) },
        )

        Modal.ConnectLocalHost -> DecisionInteractiveDialog(
            title = LocalizationR.string.interaction_warning_title.asUiText(),
            text = LocalizationR.string.interaction_warning_localhost_sub_title.asUiText(),
            confirmActionResId = LocalizationR.string.action_connect,
            dismissActionResId = LocalizationR.string.cancel,
            onConfirmAction = { processIntent(ServerSetupIntent.ConnectToLocalHost) },
            onDismissRequest = dismiss,
        )

        Modal.Background.Running -> InfoDialog(
            title = LocalizationR.string.interaction_background_running_title.asUiText(),
            subTitle = LocalizationR.string.interaction_background_running_sub_title.asUiText(),
            onDismissRequest = dismiss,
        )

        Modal.Background.Scheduled -> InfoDialog(
            title = LocalizationR.string.interaction_background_scheduled_title.asUiText(),
            subTitle = LocalizationR.string.interaction_background_scheduled_sub_title.asUiText(),
            onDismissRequest = dismiss,
        )

        is Modal.ManualPermission -> InfoDialog(
            title = LocalizationR.string.premission_rationale_title.asUiText(),
            subTitle = UiText.Resource(
                LocalizationR.string.premission_rationale_sub_title,
                screenModal.permission.asString(),
            ),
            onDismissRequest = {
                dismiss()
                context.openAppSettings()
            },
        )

        is Modal.GalleryGrid -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            GridBottomSheet(
                currentGrid = screenModal.grid,
                onSelected = {
                    processIntent(SettingsIntent.Action.GalleryGrid.Set(it))
                    dismiss()
                }
            )
        }

        is Modal.LDScheduler -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            LDSchedulerBottomSheet(
                currentScheduler = screenModal.scheduler,
                onSelected = {
                    processIntent(DebugMenuIntent.LocalDiffusionScheduler.Confirm(it))
                    dismiss()
                }
            )
        }
    }
}
