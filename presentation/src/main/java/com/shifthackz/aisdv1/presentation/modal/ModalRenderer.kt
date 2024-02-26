@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.modal

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingScreen
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingViewModel
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasScreen
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasViewModel
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageBatchResultModal
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun ModalRenderer(
    screenModal: Modal,
    onSaveGeneratedImages: (List<AiGenerationResult>) -> Unit = {},
    onViewGeneratedImage: (AiGenerationResult) -> Unit = {},
    onUpdateFromPreviousAiGeneration: (AiGenerationResult) -> Unit = {},
    onProcessLoraAlias: (String) -> Unit = {},
    onProcessHyperNet: (String) -> Unit = {},
    onProcessNewPrompts: (String, String) -> Unit = { _, _ -> },
    onDismissScreenDialog: () -> Unit = {},
) = when (screenModal) {
    Modal.None -> Unit

    is Modal.Communicating -> ProgressDialog(
        canDismiss = false,
        waitTimeSeconds = screenModal.hordeProcessStatus?.waitTimeSeconds,
        positionInQueue = screenModal.hordeProcessStatus?.queuePosition,
    )

    Modal.LoadingRandomImage -> ProgressDialog(
        titleResId = R.string.communicating_random_image_title,
        canDismiss = false,
    )

    is Modal.Error -> ErrorDialog(
        text = screenModal.error,
        onDismissRequest = onDismissScreenDialog,
    )

    is Modal.Generating -> ProgressDialog(
        titleResId = R.string.communicating_local_title,
        canDismiss = false,
        step = screenModal.pair,
    )

    is Modal.Image.Single -> GenerationImageResultDialog(
        imageBase64 = screenModal.result.image,
        showSaveButton = !screenModal.autoSaveEnabled,
        onDismissRequest = onDismissScreenDialog,
        onSaveRequest = { onSaveGeneratedImages(listOf(screenModal.result)) },
        onViewDetailRequest = { onViewGeneratedImage(screenModal.result) },
    )

    is Modal.Image.Batch -> ModalBottomSheet(
        onDismissRequest = onDismissScreenDialog,
        shape = RectangleShape,
    ) {
        GenerationImageBatchResultModal(
            screenModal.results,
            showSaveButton = !screenModal.autoSaveEnabled,
            onSaveRequest = { onSaveGeneratedImages(screenModal.results) },
            onViewDetailRequest = onViewGeneratedImage,
        )
    }

    is Modal.PromptBottomSheet -> ModalBottomSheet(
        onDismissRequest = onDismissScreenDialog,
        shape = RectangleShape,
    ) {
        InputHistoryScreen(
            viewModel = koinViewModel(),
            onGenerationSelected = { ai ->
                onUpdateFromPreviousAiGeneration(ai)
                onDismissScreenDialog()
            },
        ).Build()
    }

    is Modal.ExtraBottomSheet -> ExtrasScreen(
        viewModel = koinViewModel<ExtrasViewModel>().apply {
            updateData(screenModal.prompt, screenModal.type)
        },
        onLoraSelected = {
            when (it.type) {
                ExtraType.Lora -> it.alias?.let(onProcessLoraAlias::invoke)
                ExtraType.HyperNet -> onProcessHyperNet(it.name)
            }
            onDismissScreenDialog()
        },
        onClose = onDismissScreenDialog,
    ).Build()

    is Modal.Embeddings -> EmbeddingScreen(
        viewModel = koinViewModel<EmbeddingViewModel>().apply {
            updateData(screenModal.prompt, screenModal.negativePrompt)
        },
        onNewPrompts = onProcessNewPrompts,
        onClose = onDismissScreenDialog,
    ).Build()
}
