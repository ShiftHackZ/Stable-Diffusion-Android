@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasScreen
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageBatchResultModal
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialogCancelButton

@Composable
fun ModalRenderer(
    screenModal: Modal,
    processIntent: (GenerationMviIntent) -> Unit,
) = when (screenModal) {
    Modal.None -> Unit

    is Modal.Communicating -> ProgressDialog(
        canDismiss = false,
        waitTimeSeconds = screenModal.hordeProcessStatus?.waitTimeSeconds,
        positionInQueue = screenModal.hordeProcessStatus?.queuePosition,
    ) {
        ProgressDialogCancelButton {
            processIntent(GenerationMviIntent.Cancel.Generation)
        }
    }

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
        onDismissRequest = {
            processIntent(GenerationMviIntent.SetModal(Modal.None))
        },
    )

    is Modal.Generating -> ProgressDialog(
        titleResId = R.string.communicating_local_title,
        canDismiss = false,
        step = screenModal.pair,
    )

    is Modal.Image.Single -> GenerationImageResultDialog(
        imageBase64 = screenModal.result.image,
        showSaveButton = !screenModal.autoSaveEnabled,
        onDismissRequest = {
            processIntent(GenerationMviIntent.SetModal(Modal.None))
        },
        onSaveRequest = {
            processIntent(GenerationMviIntent.Result.Save(listOf(screenModal.result)))
        },
        onViewDetailRequest = {
            processIntent(GenerationMviIntent.Result.View(screenModal.result))
        },
    )

    is Modal.Image.Batch -> ModalBottomSheet(
        onDismissRequest = {
            processIntent(GenerationMviIntent.SetModal(Modal.None))
        },
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
        onDismissRequest = {
            processIntent(GenerationMviIntent.SetModal(Modal.None))
        },
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
        onClose = {
            processIntent(GenerationMviIntent.SetModal(Modal.None))
        },
    )

    is Modal.Embeddings -> EmbeddingScreen(
        prompt = screenModal.prompt,
        negativePrompt = screenModal.negativePrompt,
        onNewPrompts = { p, n ->
            processIntent(GenerationMviIntent.NewPrompts(p, n))
        },
        onClose = {
            processIntent(GenerationMviIntent.SetModal(Modal.None))
        },
    )
}
