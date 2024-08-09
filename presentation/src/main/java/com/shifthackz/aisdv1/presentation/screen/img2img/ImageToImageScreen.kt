@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

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
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.core.GenerationMviIntent
import com.shifthackz.aisdv1.presentation.core.ImageToImageIntent
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.screen.inpaint.components.InPaintComponent
import com.shifthackz.aisdv1.presentation.theme.sliderColors
import com.shifthackz.aisdv1.presentation.utils.Constants.DENOISING_STRENGTH_MAX
import com.shifthackz.aisdv1.presentation.utils.Constants.DENOISING_STRENGTH_MIN
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm
import com.shifthackz.aisdv1.presentation.widget.toolbar.GenerationBottomToolbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import com.shz.imagepicker.imagepicker.ImagePicker
import com.shz.imagepicker.imagepicker.model.GalleryPicker
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ImageToImageScreen() {
    val context = LocalContext.current
    val viewModel = koinViewModel<ImageToImageViewModel>()
    val fileProviderDescriptor: FileProviderDescriptor = koinInject()
    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            ImagePicker.Builder(fileProviderDescriptor.providerPath) { result ->
                viewModel.processIntent(ImageToImageIntent.CropImage(result))
            }
                .useGallery(effect == ImageToImageEffect.GalleryPicker)
                .useCamera(effect == ImageToImageEffect.CameraPicker)
                .autoRotate(effect == ImageToImageEffect.GalleryPicker)
                .multipleSelection(false)
                .galleryPicker(GalleryPicker.NATIVE)
                .build()
                .launch(context)
        },
        applySystemUiColors = false,
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
    val promptChipTextFieldState = remember { mutableStateOf(TextFieldValue()) }
    val negativePromptChipTextFieldState = remember { mutableStateOf(TextFieldValue()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    Box(modifier) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                processIntent(GenerationMviIntent.Drawer(DrawerIntent.Open))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(id = LocalizationR.string.title_image_to_image),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        actions = {
                            if (state.mode != ServerSource.LOCAL) {
                                IconButton(
                                    onClick = {
                                        processIntent(
                                            GenerationMviIntent.SetModal(
                                                Modal.PromptBottomSheet(
                                                    AiGenerationResult.Type.IMAGE_TO_IMAGE,
                                                ),
                                            ),
                                        )
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
                    BackgroundWorkWidget(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 4.dp),
                    )
                }
            },
            content = { paddingValues ->
                when (state.mode) {
                    ServerSource.AUTOMATIC1111,
                    ServerSource.SWARM_UI,
                    ServerSource.HORDE,
                    ServerSource.STABILITY_AI,
                    ServerSource.HUGGING_FACE -> {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .padding(paddingValues)
                                .verticalScroll(scrollState)
                        ) {
                            InputImageState(
                                modifier = Modifier.fillMaxWidth(),
                                state = state,
                                processIntent = processIntent
                            )
                            GenerationInputForm(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                state = state,
                                isImg2Img = true,
                                processIntent = processIntent,
                                promptChipTextFieldState = promptChipTextFieldState,
                                negativePromptChipTextFieldState = negativePromptChipTextFieldState,
                                afterSlidersSection = {
                                    Text(
                                        modifier = Modifier.padding(top = 8.dp),
                                        text = stringResource(
                                            id = LocalizationR.string.hint_denoising_strength,
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

                    else -> {
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
                                text = stringResource(id = LocalizationR.string.local_no_img2img_support_title),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                modifier = Modifier.padding(top = 14.dp),
                                text = stringResource(
                                    if (state.mode == ServerSource.LOCAL) LocalizationR.string.local_no_img2img_support_sub_title
                                    else LocalizationR.string.dalle_no_img2img_support_sub_title
                                ),
                            )
                            Text(
                                modifier = Modifier.padding(top = 14.dp),
                                text = stringResource(
                                    if (state.mode == ServerSource.LOCAL) LocalizationR.string.local_no_img2img_support_sub_title_2
                                    else LocalizationR.string.dalle_no_img2img_support_sub_title_2
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
                            keyboardController?.hide()
                            when (state.mode) {
                                ServerSource.OPEN_AI,
                                ServerSource.LOCAL -> processIntent(GenerationMviIntent.Configuration)

                                else -> {
                                    promptChipTextFieldState.value.text.takeIf(String::isNotBlank)
                                        ?.let { "${state.prompt}, ${it.trim()}" }
                                        ?.let(GenerationMviIntent.Update::Prompt)
                                        ?.let(processIntent::invoke)
                                        ?.also { promptChipTextFieldState.value = TextFieldValue("") }

                                    negativePromptChipTextFieldState.value.text.takeIf(String::isNotBlank)
                                        ?.let { "${state.negativePrompt}, ${it.trim()}" }
                                        ?.let(GenerationMviIntent.Update::NegativePrompt)
                                        ?.let(processIntent::invoke)
                                        ?.also { negativePromptChipTextFieldState.value = TextFieldValue("") }

                                    processIntent(GenerationMviIntent.Generate)
                                }
                            }
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
                                    ServerSource.OPEN_AI -> LocalizationR.string.action_change_configuration

                                    else -> LocalizationR.string.action_generate
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
            processIntent = { (it as? GenerationMviIntent)?.let(processIntent::invoke) },
        )
    }
}

@Composable
private fun InputImageState(
    modifier: Modifier = Modifier,
    state: ImageToImageState,
    processIntent: (GenerationMviIntent) -> Unit = {},
) {
    when (state.imageState) {
        is ImageToImageState.ImageState.Image -> Column(
            modifier = modifier,
        ) {
            InPaintComponent(
                drawMode = false,
                bitmap = state.imageState.bitmap,
                inPaint = state.inPaintModel,
            )
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
            ) {
                if (state.mode == ServerSource.AUTOMATIC1111) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = { processIntent(ImageToImageIntent.InPaint) },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Draw,
                            contentDescription = null,
                            tint = LocalContentColor.current,
                        )
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            text = stringResource(id = LocalizationR.string.in_paint_title),
                            color = LocalContentColor.current,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = { processIntent(ImageToImageIntent.ClearImageInput) },
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = LocalContentColor.current,
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = LocalizationR.string.action_clear),
                        color = LocalContentColor.current,
                    )
                }
            }
        }

        ImageToImageState.ImageState.None -> Column(
            modifier = modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp),
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
                    .clip(RoundedCornerShape(16.dp))
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
                    text = stringResource(id = LocalizationR.string.action_image_picker_random),
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
    val localShape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(localShape)
            .background(
                MaterialTheme.colorScheme.surfaceTint,
                shape = localShape,
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
                    ImagePickButton.PHOTO -> LocalizationR.string.action_image_picker_gallery
                    ImagePickButton.CAMERA -> LocalizationR.string.action_image_picker_camera
                }
            ),
            fontSize = 17.sp,
        )
    }
}
