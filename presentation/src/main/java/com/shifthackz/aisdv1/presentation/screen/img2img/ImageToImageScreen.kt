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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.ImageToImageIntent
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants.DENOISING_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.DENOISING_STRENGTH_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.toolbar.GenerationBottomToolbar
import com.shz.imagepicker.imagepicker.ImagePicker
import com.shz.imagepicker.imagepicker.model.GalleryPicker
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ImageToImageScreen() {
    val context = LocalContext.current
    val viewModel = koinViewModel<ImageToImageViewModel>()
    val fileProviderDescriptor: FileProviderDescriptor = koinInject()
    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            ImagePicker.Builder(fileProviderDescriptor.providerPath) { result ->
                viewModel.processIntent(ImageToImageIntent.UpdateImage(result))
            }
                .useGallery(effect == ImageToImageEffect.GalleryPicker)
                .useCamera(effect == ImageToImageEffect.CameraPicker)
                .autoRotate(effect == ImageToImageEffect.GalleryPicker)
                .multipleSelection(false)
                .galleryPicker(GalleryPicker.NATIVE)
                .build()
                .launch(context)
        },
    ) { state, intentHandler ->
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            processIntent = intentHandler,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ImageToImageState,
    processIntent: (GenerationMviIntent) -> Unit = {}
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
                        if (state.mode != ServerSource.LOCAL) {
                            IconButton(
                                onClick = {
                                    processIntent(GenerationMviIntent.SetModal(Modal.PromptBottomSheet))
                                },
                            ) {
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
                when (state.mode) {
                    ServerSource.AUTOMATIC1111,
                    ServerSource.HORDE,
                    ServerSource.HUGGING_FACE -> {
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
                                processIntent = processIntent
                            )
                            GenerationInputForm(
                                state = state,
                                processIntent = processIntent,
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
                                            processIntent(
                                                ImageToImageIntent.UpdateDenoisingStrength(
                                                    it.roundTo(2)
                                                )
                                            )
                                        },
                                    )
                                }
                            )
                        }
                    }

                    ServerSource.OPEN_AI,
                    ServerSource.LOCAL -> {
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
                                text = stringResource(
                                    if (state.mode == ServerSource.LOCAL) R.string.local_no_img2img_support_sub_title
                                    else R.string.dalle_no_img2img_support_sub_title
                                ),
                            )
                            Text(
                                modifier = Modifier.padding(top = 14.dp),
                                text = stringResource(
                                    if (state.mode == ServerSource.LOCAL) R.string.local_no_img2img_support_sub_title_2
                                    else R.string.dalle_no_img2img_support_sub_title_2
                                ),
                            )
                        }
                    }
                }
            },
            bottomBar = {
                val isEnabled = when (state.mode) {
                    ServerSource.LOCAL,
                    ServerSource.OPEN_AI -> true

                    else -> !state.hasValidationErrors && !state.imageState.isEmpty
                }
                GenerationBottomToolbar(
                    state = state,
                    strokeAccentState = isEnabled,
                    processIntent = processIntent,
                ) {
                    Button(
                        modifier = Modifier
                            .height(height = 60.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        onClick = {
                            processIntent(
                                when (state.mode) {
                                    ServerSource.LOCAL,
                                    ServerSource.OPEN_AI -> GenerationMviIntent.Configuration

                                    else -> GenerationMviIntent.Generate
                                }
                            )
                        },
                        enabled = isEnabled,
                    ) {
                        if (state.mode != ServerSource.LOCAL) {
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
                                    ServerSource.LOCAL,
                                    ServerSource.OPEN_AI -> R.string.action_change_configuration

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
            processIntent = processIntent,
        )
    }
}

@Composable
private fun InputImageState(
    modifier: Modifier = Modifier,
    imageState: ImageToImageState.ImageState,
    processIntent: (GenerationMviIntent) -> Unit = {},
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
                onClick = { processIntent(ImageToImageIntent.ClearImageInput) },
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
                    onClick = { processIntent(ImageToImageIntent.Pick.Gallery) },
                )
                ImagePickButtonBox(
                    modifier = pickButtonModifier,
                    buttonType = ImagePickButton.CAMERA,
                    onClick = { processIntent(ImageToImageIntent.Pick.Camera) },
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
                    .clickable { processIntent(ImageToImageIntent.FetchRandomPhoto) },
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
