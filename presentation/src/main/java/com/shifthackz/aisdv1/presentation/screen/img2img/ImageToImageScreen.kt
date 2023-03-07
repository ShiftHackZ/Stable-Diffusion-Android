@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.GenerationImageResultDialog
import com.shifthackz.aisdv1.presentation.widget.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.ProgressDialog
import com.shz.imagepicker.imagepicker.ImagePickerCallback

class ImageToImageScreen(
    private val viewModel: ImageToImageViewModel,
    private val pickImage: (ImagePickerCallback) -> Unit,
    private val takePhoto: (ImagePickerCallback) -> Unit,
) : MviScreen<ImageToImageState, ImageToImageEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onPickImage = { pickImage(viewModel::updateInputImage) },
            onTakePhoto = { takePhoto(viewModel::updateInputImage) },
            onClearPhoto = viewModel::clearInputImage,
            onPromptUpdated = viewModel::updatePrompt,
            onNegativePromptUpdated = viewModel::updateNegativePrompt,
            onWidthUpdated = viewModel::updateWidth,
            onHeightUpdated = viewModel::updateHeight,
            onSamplingStepsUpdated = viewModel::updateSamplingSteps,
            onCfgScaleUpdated = viewModel::updateCfgScale,
            onRestoreFacesUpdated = viewModel::updateRestoreFaces,
            onSeedUpdated = viewModel::updateSeed,
            onSamplerUpdated = viewModel::updateSampler,
            onGenerateClicked = viewModel::generate,
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
    onClearPhoto: () -> Unit = {},
    onPromptUpdated: (String) -> Unit = {},
    onNegativePromptUpdated: (String) -> Unit = {},
    onWidthUpdated: (String) -> Unit = {},
    onHeightUpdated: (String) -> Unit = {},
    onSamplingStepsUpdated: (Int) -> Unit = {},
    onCfgScaleUpdated: (Float) -> Unit = {},
    onRestoreFacesUpdated: (Boolean) -> Unit = {},
    onSeedUpdated: (String) -> Unit = {},
    onSamplerUpdated: (String) -> Unit = {},

    onGenerateClicked: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
//                        Text(stringResource(id = R.string.title_text_to_image))
                        Text("Image to Image")
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
                        onPromptUpdated = onPromptUpdated,
                        onNegativePromptUpdated = onNegativePromptUpdated,
                        onWidthUpdated = onWidthUpdated,
                        onHeightUpdated = onHeightUpdated,
                        onSamplingStepsUpdated = onSamplingStepsUpdated,
                        onCfgScaleUpdated = onCfgScaleUpdated,
                        onRestoreFacesUpdated = onRestoreFacesUpdated,
                        onSeedUpdated = onSeedUpdated,
                        onSamplerUpdated = onSamplerUpdated,
                        widthValidationError = state.widthValidationError,
                        heightValidationError = state.heightValidationError,
                    )
                }
            },
            bottomBar = {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 16.dp),
                    onClick = onGenerateClicked,
                    enabled = !state.hasValidationErrors && !state.imageState.isEmpty
                ) {
                    Text(
                        text = stringResource(id = R.string.action_generate)
                    )
                }
            }
        )
        when (state.screenDialog) {
            ImageToImageState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )
            is ImageToImageState.Dialog.Error -> ErrorDialog(
                text = state.screenDialog.error,
                onDismissScreenDialog,
            )
            is ImageToImageState.Dialog.Image -> GenerationImageResultDialog(
                state.screenDialog.image,
                onDismissScreenDialog,
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
            .background(Color.LightGray, shape = RoundedCornerShape(16.dp))
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
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = when (buttonType) {
                ImagePickButton.PHOTO -> "Choose photo"
                ImagePickButton.CAMERA -> "Take new photo"
            },
            fontSize = 17.sp,
        )
    }
}
