@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.coins.AvailableCoinsComposable
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.NoSdAiCoinsDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shz.imagepicker.imagepicker.ImagePickerCallback
import org.koin.androidx.compose.koinViewModel

class ImageToImageScreen(
    private val viewModel: ImageToImageViewModel,
    private val pickImage: (ImagePickerCallback) -> Unit,
    private val takePhoto: (ImagePickerCallback) -> Unit,
    private val launchRewarded: () -> Unit,
) : MviScreen<ImageToImageState, ImageToImageEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onPickImage = { pickImage(viewModel::updateInputImage) },
            onTakePhoto = { takePhoto(viewModel::updateInputImage) },
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
            onGenerateClicked = viewModel::generate,
            onSaveGeneratedImage = viewModel::saveGeneratedResult,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
            onLaunchRewarded = launchRewarded,
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
    onGenerateClicked: () -> Unit = {},
    onSaveGeneratedImage: (AiGenerationResult) -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
    onLaunchRewarded: () -> Unit = {},
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
                )
            },
            content = { paddingValues ->
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp),
                ) {
                    InputImageState(
                        modifier = Modifier.fillMaxWidth(),
                        imageState = state.imageState,
                        onPickImage = onPickImage,
                        onTakePhoto = onTakePhoto,
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
            },
            bottomBar = {
                Column(Modifier.fillMaxWidth()) {
                    AvailableCoinsComposable(
                        modifier = Modifier.fillMaxWidth(),
                        viewModel = koinViewModel()
                    ).Build()
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
            }
        )
        when (state.screenDialog) {
            ImageToImageState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )
            ImageToImageState.Dialog.NoSdAiCoins -> NoSdAiCoinsDialog(
                onDismissRequest = onDismissScreenDialog,
                launchRewarded = onLaunchRewarded,
            )
            is ImageToImageState.Dialog.Error -> ErrorDialog(
                text = state.screenDialog.error,
                onDismissRequest = onDismissScreenDialog,
            )
            is ImageToImageState.Dialog.Image -> GenerationImageResultDialog(
                imageBase64 = state.screenDialog.result.image,
                showSaveButton = !state.screenDialog.autoSaveEnabled,
                onDismissRequest = onDismissScreenDialog,
                onSaveRequest = { onSaveGeneratedImage(state.screenDialog.result) },
            )
            ImageToImageState.Dialog.None -> Unit
        }
    }
}

@Composable
private fun InputImageState(
    modifier: Modifier = Modifier,
    imageState: ImageToImageState.ImageState,
    onPickImage: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
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
        ImageToImageState.ImageState.None -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val pickButtonModifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .aspectRatio(1f)
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
                MaterialTheme.colorScheme.secondaryContainer,
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
                ImagePickButton.PHOTO -> Icons.Default.BrowseGallery
                ImagePickButton.CAMERA -> Icons.Default.Camera
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = when (buttonType) {
                ImagePickButton.PHOTO -> "Choose photo"
                ImagePickButton.CAMERA -> "Take new photo"
            },
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
    }
}
