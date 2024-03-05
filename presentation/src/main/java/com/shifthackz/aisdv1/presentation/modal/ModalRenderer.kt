@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasScreen
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailIntent
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryIntent
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageBatchResultModal
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialogCancelButton
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.android.core.mvi.MviIntent

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
    }
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
            titleResId = R.string.communicating_random_image_title,
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
            titleResId = R.string.communicating_local_title,
            canDismiss = false,
            step = screenModal.pair,
        )

        is Modal.Image.Single -> GenerationImageResultDialog(
            imageBase64 = screenModal.result.image,
            showSaveButton = !screenModal.autoSaveEnabled,
            onDismissRequest = dismiss,
            onSaveRequest = {
                processIntent(GenerationMviIntent.Result.Save(listOf(screenModal.result)))
            },
            onViewDetailRequest = {
                processIntent(GenerationMviIntent.Result.View(screenModal.result))
            },
        )

        is Modal.Image.Batch -> ModalBottomSheet(
            onDismissRequest = dismiss,
            shape = RectangleShape,
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
        ) {
            InputHistoryScreen(
                onGenerationSelected = { ai ->
                    processIntent(GenerationMviIntent.UpdateFromGeneration(ai))
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
            title = R.string.title_clear_app_cache.asUiText(),
            text = R.string.interaction_cache_sub_title.asUiText(),
            confirmActionResId = R.string.yes,
            dismissActionResId = R.string.no,
            onDismissRequest = dismiss,
            onConfirmAction = { processIntent(SettingsIntent.Action.ClearAppCache.Confirm) },
        )

        is Modal.SelectSdModel -> {
            var selectedItem by remember { mutableStateOf(screenModal.selected) }
            DecisionInteractiveDialog(
                title = R.string.title_select_sd_model.asUiText(),
                text = UiText.empty,
                confirmActionResId = R.string.action_select,
                onConfirmAction = { processIntent(SettingsIntent.SdModel.Select(selectedItem)) },
                onDismissRequest = dismiss,
                content = {
                    DropdownTextField(
                        modifier = Modifier.fillMaxWidth(),
                        label = R.string.hint_sd_model.asUiText(),
                        value = selectedItem,
                        items = screenModal.models,
                        onItemSelected = { selectedItem = it },
                    )
                }
            )
        }

        Modal.DeleteConfirm -> DecisionInteractiveDialog(
            title = R.string.interaction_delete_generation_title.asUiText(),
            text = R.string.interaction_delete_generation_sub_title.asUiText(),
            confirmActionResId = R.string.yes,
            dismissActionResId = R.string.no,
            onConfirmAction = { processIntent(GalleryDetailIntent.Delete.Confirm) },
            onDismissRequest = dismiss,
        )

        Modal.ConfirmExport -> DecisionInteractiveDialog(
            title = R.string.interaction_export_title.asUiText(),
            text = R.string.interaction_export_sub_title.asUiText(),
            confirmActionResId = R.string.action_export,
            onConfirmAction = { processIntent(GalleryIntent.Export.Confirm) },
            onDismissRequest = dismiss,
        )

        Modal.ExportInProgress ->  ProgressDialog(
            titleResId = R.string.exporting_progress_title,
            subTitleResId = R.string.exporting_progress_sub_title,
            canDismiss = false,
        )
    }
}
