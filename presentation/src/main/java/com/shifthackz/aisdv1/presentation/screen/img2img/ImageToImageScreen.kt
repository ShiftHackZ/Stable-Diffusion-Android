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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryScreen
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputMode
import com.shz.imagepicker.imagepicker.ImagePickerCallback
import org.koin.androidx.compose.koinViewModel

class ImageToImageScreen(
    private val viewModel: ImageToImageViewModel,
    private val pickImage: (ImagePickerCallback) -> Unit,
    private val takePhoto: (ImagePickerCallback) -> Unit,
    private val launchGalleryDetail: (Long) -> Unit,
    private val launchServerSetup: () -> Unit,
) : MviScreen<ImageToImageState, ImageToImageEffect>(viewModel) {

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
            onGenerateClicked = viewModel::generate,
            onChangeConfigurationClicked = launchServerSetup,
            onSaveGeneratedImage = viewModel::saveGeneratedResult,
            onViewGeneratedImage = launchGalleryDetail,
            onOpenPreviousGenerationInput = viewModel::openPreviousGenerationInput,
            onUpdateFromPreviousAiGeneration = viewModel::updateFormPreviousAiGeneration,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
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
    onGenerateClicked: () -> Unit = {},
    onChangeConfigurationClicked: () -> Unit = {},
    onSaveGeneratedImage: (AiGenerationResult) -> Unit = {},
    onViewGeneratedImage: (Long) -> Unit = {},
    onOpenPreviousGenerationInput: () -> Unit = {},
    onUpdateFromPreviousAiGeneration: (AiGenerationResult) -> Unit = {},
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
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
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
                                    valueRange = Constants.DENOISING_STRENGTH_MIN..Constants.DENOISING_STRENGTH_MAX,
//                                steps = abs(Constants.DENOISING_STRENGTH_MAX - Constants.DENOISING_STRENGTH_MIN) * 2 - 1,
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
                if (state.mode != GenerationInputMode.LOCAL) {
                    Column(Modifier.fillMaxWidth()) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .padding(bottom = 16.dp),
                            onClick = onGenerateClicked,
                            enabled = !state.hasValidationErrors && !state.imageState.isEmpty,
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                imageVector = Icons.Default.AutoFixNormal,
                                contentDescription = "Imagine",
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = stringResource(id = R.string.action_generate)
                            )
                        }
                    }
                } else {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                            .padding(bottom = 16.dp),
                        onClick = onChangeConfigurationClicked,
                    ) {
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(id = R.string.action_change_configuration),
                            color = LocalContentColor.current,
                        )
                    }
                }
            }
        )
        when (state.screenModal) {
            is ImageToImageState.Modal.Communicating -> ProgressDialog(
                canDismiss = false,
                waitTimeSeconds = state.screenModal.hordeProcessStatus?.waitTimeSeconds,
                positionInQueue = state.screenModal.hordeProcessStatus?.queuePosition,
            )
            ImageToImageState.Modal.LoadingRandomImage -> ProgressDialog(
                titleResId = R.string.communicating_random_image_title,
                canDismiss = false,
            )
            is ImageToImageState.Modal.Error -> ErrorDialog(
                text = state.screenModal.error,
                onDismissRequest = onDismissScreenDialog,
            )
            is ImageToImageState.Modal.Image -> GenerationImageResultDialog(
                imageBase64 = state.screenModal.result.image,
                showSaveButton = !state.screenModal.autoSaveEnabled,
                onDismissRequest = onDismissScreenDialog,
                onSaveRequest = { onSaveGeneratedImage(state.screenModal.result) },
                onViewDetailRequest = { onViewGeneratedImage(state.screenModal.result.id) },
            )
            is ImageToImageState.Modal.PromptBottomSheet -> ModalBottomSheet(
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
            else -> Unit
        }
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
