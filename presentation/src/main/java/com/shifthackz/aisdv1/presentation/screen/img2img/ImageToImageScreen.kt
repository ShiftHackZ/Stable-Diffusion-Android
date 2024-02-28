@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArtTrack
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeviceUnknown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviEffect
import com.shifthackz.aisdv1.presentation.core.GenerationMviScreen
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants.DENOISING_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.DENOISING_STRENGTH_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode
import com.shifthackz.aisdv1.presentation.widget.toolbar.GenerationBottomToolbar
import com.shz.imagepicker.imagepicker.ImagePickerCallback

class ImageToImageScreen(
    private val viewModel: ImageToImageViewModel,
    private val pickImage: (ImagePickerCallback) -> Unit,
    private val takePhoto: (ImagePickerCallback) -> Unit,
    private val launchGalleryDetail: (Long) -> Unit,
    private val launchServerSetup: () -> Unit,
) : GenerationMviScreen<ImageToImageState, GenerationMviEffect>(
    viewModel,
    launchGalleryDetail,
) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onPickImage = { pickImage(viewModel::updateInputImage) },
            onTakePhoto = { takePhoto(viewModel::updateInputImage) },
            onRandomPhoto = viewModel::fetchRandomImage,
            onClearPhoto = viewModel::clearInputImage,
            onDenoisingStrengthUpdated = viewModel::updateDenoisingStrength,
            onShowAdvancedOptionsToggle = viewModel::toggleAdvancedOptions,
            onPromptUpdated = viewModel::updatePrompt,
            onNegativePromptUpdated = viewModel::updateNegativePrompt,
            onWidthUpdated = viewModel::updateWidth,
            onHeightUpdated = viewModel::updateHeight,
            onSamplingStepsUpdated = viewModel::updateSamplingSteps,
            onCfgScaleUpdated = viewModel::updateCfgScale,
            onRestoreFacesUpdated = viewModel::updateRestoreFaces,
            onSeedUpdated = viewModel::updateSeed,
            onSubSeedUpdated = viewModel::updateSubSeed,
            onSubSeedStrengthUpdated = viewModel::updateSubSeedStrength,
            onSamplerUpdated = viewModel::updateSampler,
            onNsfwUpdated = viewModel::updateNsfw,
            onBatchCountUpdated = viewModel::updateBatchCount,
            onGenerateClicked = viewModel::generate,
            onChangeConfigurationClicked = launchServerSetup,
            onSaveGeneratedImages = viewModel::saveGeneratedResults,
            onViewGeneratedImage = viewModel::viewGeneratedResult,
            onOpenPreviousGenerationInput = viewModel::openPreviousGenerationInput,
            onUpdateFromPreviousAiGeneration = viewModel::updateFormPreviousAiGeneration,
            onOpenLoraInput = viewModel::openLoraInput,
            onOpenHyperNetInput = viewModel::openHyperNetInput,
            onOpenEmbedding = viewModel::openEmbeddingInput,
            onProcessNewPrompts = viewModel::processNewPrompts,
            onCancelGeneration = viewModel::cancelGeneration,
            onCancelFetchRandomImage = viewModel::cancelFetchRandomImage,
            onDismissScreenDialog = viewModel::dismissScreenModal,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ImageToImageState,
    onPickImage: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onRandomPhoto: () -> Unit = {},
    onClearPhoto: () -> Unit = {},
    onDenoisingStrengthUpdated: (Float) -> Unit,
    onShowAdvancedOptionsToggle: (Boolean) -> Unit = {},
    onPromptUpdated: (String) -> Unit = {},
    onNegativePromptUpdated: (String) -> Unit = {},
    onWidthUpdated: (String) -> Unit = {},
    onHeightUpdated: (String) -> Unit = {},
    onSamplingStepsUpdated: (Int) -> Unit = {},
    onCfgScaleUpdated: (Float) -> Unit = {},
    onRestoreFacesUpdated: (Boolean) -> Unit = {},
    onSeedUpdated: (String) -> Unit = {},
    onSubSeedUpdated: (String) -> Unit = {},
    onSubSeedStrengthUpdated: (Float) -> Unit = {},
    onSamplerUpdated: (String) -> Unit = {},
    onNsfwUpdated: (Boolean) -> Unit = {},
    onBatchCountUpdated: (Int) -> Unit = {},
    onGenerateClicked: () -> Unit = {},
    onChangeConfigurationClicked: () -> Unit = {},
    onSaveGeneratedImages: (List<AiGenerationResult>) -> Unit = {},
    onViewGeneratedImage: (AiGenerationResult) -> Unit = {},
    onOpenPreviousGenerationInput: () -> Unit = {},
    onUpdateFromPreviousAiGeneration: (AiGenerationResult) -> Unit = {},
    onOpenLoraInput: () -> Unit = {},
    onOpenHyperNetInput: () -> Unit = {},
    onOpenEmbedding: () -> Unit = {},
    onProcessNewPrompts: (String, String) -> Unit = { _, _ -> },
    onCancelGeneration: () -> Unit = {},
    onCancelFetchRandomImage: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_image_to_image),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    actions = {
                        if (state.mode != GenerationInputMode.LOCAL) {
                            IconButton(onClick = onOpenPreviousGenerationInput) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                )
                            }
                        }
                    },
                )
            },
            content = { paddingValues ->
                if (state.mode != GenerationInputMode.LOCAL) {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp),
                    ) {
                        InputImageState(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            imageState = state.imageState,
                            onPickImage = onPickImage,
                            onTakePhoto = onTakePhoto,
                            onRandomPhoto = onRandomPhoto,
                            onClearPhoto = onClearPhoto,
                        )
                        GenerationInputForm(
                            state = state,
                            onShowAdvancedOptionsToggle = onShowAdvancedOptionsToggle,
                            onPromptUpdated = onPromptUpdated,
                            onNegativePromptUpdated = onNegativePromptUpdated,
                            onWidthUpdated = onWidthUpdated,
                            onHeightUpdated = onHeightUpdated,
                            onSamplingStepsUpdated = onSamplingStepsUpdated,
                            onCfgScaleUpdated = onCfgScaleUpdated,
                            onRestoreFacesUpdated = onRestoreFacesUpdated,
                            onSeedUpdated = onSeedUpdated,
                            onSubSeedUpdated = onSubSeedUpdated,
                            onSubSeedStrengthUpdated = onSubSeedStrengthUpdated,
                            onSamplerUpdated = onSamplerUpdated,
                            onNsfwUpdated = onNsfwUpdated,
                            onBatchCountUpdated = onBatchCountUpdated,
                            widthValidationError = state.widthValidationError,
                            heightValidationError = state.heightValidationError,
                            afterSlidersSection = {
                                Text(
                                    modifier = Modifier.padding(top = 8.dp),
                                    text = stringResource(
                                        id = R.string.hint_denoising_strength,
                                        "${state.denoisingStrength.roundTo(2)}"
                                    ),
                                )
                                Slider(
                                    value = state.denoisingStrength,
                                    valueRange = DENOISING_STRENGTH_MIN..DENOISING_STRENGTH_MAX,
                                    colors = sliderColors,
                                    onValueChange = {
                                        onDenoisingStrengthUpdated(it.roundTo(2))
                                    },
                                )
                            }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .padding(horizontal = 36.dp)
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            modifier = Modifier.size(96.dp),
                            imageVector = Icons.Default.DeviceUnknown,
                            contentDescription = "Feature not supported",
                        )
                        Text(
                            modifier = Modifier.padding(top = 20.dp),
                            text = stringResource(id = R.string.local_no_img2img_support_title),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            modifier = Modifier.padding(top = 14.dp),
                            text = stringResource(id = R.string.local_no_img2img_support_sub_title),
                        )
                        Text(
                            modifier = Modifier.padding(top = 14.dp),
                            text = stringResource(id = R.string.local_no_img2img_support_sub_title_2),
                        )
                    }
                }
            },
            bottomBar = {
                val isEnabled = when (state.mode) {
                    GenerationInputMode.LOCAL -> true
                    else -> !state.hasValidationErrors && !state.imageState.isEmpty
                }
                GenerationBottomToolbar(
                    showToolbar = state.mode == GenerationInputMode.AUTOMATIC1111,
                    strokeAccentState = isEnabled,
                    onOpenLoraInput = onOpenLoraInput,
                    onOpenHyperNetInput = onOpenHyperNetInput,
                    onOpenEmbedding = onOpenEmbedding,
                ) {
                    Button(
                        modifier = Modifier
                            .height(height = 60.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        onClick = when (state.mode) {
                            GenerationInputMode.LOCAL -> onChangeConfigurationClicked
                            else -> onGenerateClicked
                        },
                        enabled = isEnabled,
                    ) {
                        if (state.mode != GenerationInputMode.LOCAL) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                imageVector = Icons.Default.AutoFixNormal,
                                contentDescription = "Imagine",
                            )
                        }
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(
                                id = when (state.mode) {
                                    GenerationInputMode.LOCAL -> R.string.action_change_configuration
                                    else -> R.string.action_generate
                                }
                            ),
                            color = LocalContentColor.current,
                        )
                    }
                }
            }
        )
        ModalRenderer(
            screenModal = state.screenModal,
            onSaveGeneratedImages = onSaveGeneratedImages,
            onViewGeneratedImage = onViewGeneratedImage,
            onUpdateFromPreviousAiGeneration = onUpdateFromPreviousAiGeneration,
            onProcessNewPrompts = onProcessNewPrompts,
            onCancelGeneration = onCancelGeneration,
            onCancelFetchRandomImage = onCancelFetchRandomImage,
            onDismissScreenDialog = onDismissScreenDialog,
        )
    }
}

@Composable
private fun InputImageState(
    modifier: Modifier = Modifier,
    imageState: ImageToImageState.ImageState,
    onPickImage: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onRandomPhoto: () -> Unit = {},
    onClearPhoto: () -> Unit = {},
) {
    when (imageState) {
        is ImageToImageState.ImageState.Image -> Box(
            modifier = modifier,
            contentAlignment = Alignment.TopEnd,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.Center),
                bitmap = imageState.bitmap.asImageBitmap(),
                contentDescription = "img2img input",
                contentScale = ContentScale.Crop,
            )
            IconButton(
                modifier = Modifier
                    .padding(4.dp)
                    .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp)),
                onClick = onClearPhoto,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.DarkGray,
                )
            }
        }
        ImageToImageState.ImageState.None -> Column(
            modifier = modifier,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val pickButtonModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(1.35f)
                ImagePickButtonBox(
                    modifier = pickButtonModifier,
                    buttonType = ImagePickButton.PHOTO,
                    onClick = onPickImage,
                )
                ImagePickButtonBox(
                    modifier = pickButtonModifier,
                    buttonType = ImagePickButton.CAMERA,
                    onClick = onTakePhoto,
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceTint,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onRandomPhoto() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp),
                    imageVector = Icons.Default.ArtTrack,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(id = R.string.action_image_picker_random),
                    fontSize = 17.sp,
                )
            }
        }
    }
}

@Composable
private fun ImagePickButtonBox(
    modifier: Modifier,
    buttonType: ImagePickButton,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceTint,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp)
                .padding(top = 16.dp),
            imageVector = when (buttonType) {
                ImagePickButton.PHOTO -> Icons.Default.Image
                ImagePickButton.CAMERA -> Icons.Default.Camera
            },
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(
                id = when (buttonType) {
                    ImagePickButton.PHOTO -> R.string.action_image_picker_gallery
                    ImagePickButton.CAMERA -> R.string.action_image_picker_camera
                }
            ),
            fontSize = 17.sp,
        )
    }
}
