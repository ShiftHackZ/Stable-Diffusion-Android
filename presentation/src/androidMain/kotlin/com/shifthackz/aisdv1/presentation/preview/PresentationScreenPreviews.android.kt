package com.shifthackz.aisdv1.presentation.preview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiSampler
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailContent
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreenContent
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailState
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailTab
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageContent
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageState
import com.shifthackz.aisdv1.presentation.screen.settings.ContentSettingsState
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsState
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupContent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageContent
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageState
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppTheme
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppThemeState
import com.shifthackz.aisdv1.presentation.widget.input.GenerationInputForm

@Preview(name = "Txt2Img screen", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun TextToImageContentPreview() {
    PreviewTheme {
        TextToImageContent(
            state = previewTextToImageState(),
            processIntent = {},
            useDrawerNavigation = true,
        )
    }
}

@Preview(name = "Img2Img unsupported", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun ImageToImageUnsupportedPreview() {
    PreviewTheme {
        ImageToImageContent(
            state = previewImageToImageState().copy(mode = ServerSource.OPEN_AI),
            processIntent = {},
            useDrawerNavigation = true,
        )
    }
}

@Preview(name = "Generation form", widthDp = 360, heightDp = 560, showBackground = true)
@Composable
private fun GenerationInputFormPreview() {
    PreviewTheme {
        GenerationInputForm(
            modifier = Modifier.padding(16.dp),
            state = previewTextToImageState(),
            promptChipTextFieldState = remember { mutableStateOf(TextFieldValue()) },
            negativePromptChipTextFieldState = remember { mutableStateOf(TextFieldValue()) },
        )
    }
}

@Preview(name = "Configuration", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun ServerSetupContentPreview() {
    PreviewTheme {
        ServerSetupContent(
            state = ServerSetupState(
                showBackNavArrow = true,
                step = ServerSetupState.Step.CONFIGURE,
                loadingConfiguration = false,
                serverUrl = "https://a1111.svc.home.arpa",
                demoMode = true,
            ),
            processIntent = {},
        )
    }
}

@Preview(name = "Settings content", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun SettingsContentPreview() {
    PreviewTheme {
        ContentSettingsState(
            modifier = Modifier.fillMaxSize(),
            state = SettingsState(
                loading = false,
                serverSource = ServerSource.AUTOMATIC1111,
                sdModels = listOf("sd15-pruned-emaonly.safetensors"),
                sdModelSelected = "sd15-pruned-emaonly.safetensors",
                backgroundGenerationAvailable = true,
                monitorConnectivity = true,
                autoSaveAiResults = true,
                saveToMediaStore = true,
                appVersion = "1.0.0 (200)",
            ),
        )
    }
}

@Preview(name = "Gallery details info", widthDp = 360, heightDp = 740, showBackground = true)
@Composable
private fun GalleryDetailInfoPreview() {
    PreviewTheme {
        GalleryDetailScreenContent(
            state = GalleryDetailState(
                loading = false,
                tabs = listOf(GalleryDetailTab.IMAGE, GalleryDetailTab.INFO),
                selectedTab = GalleryDetailTab.INFO,
                content = GalleryDetailContent(
                    showReportButton = true,
                    generationType = AiGenerationResult.Type.TEXT_TO_IMAGE,
                    id = 1L,
                    imageBase64 = "",
                    image = null,
                    inputImageBase64 = null,
                    inputImage = null,
                    createdAt = "2026-06-10 16:30",
                    type = "txt2img",
                    prompt = "cinematic mobile app preview",
                    negativePrompt = "low quality",
                    size = "512 X 512",
                    samplingSteps = "20",
                    cfgScale = "7.0",
                    restoreFaces = "No",
                    sampler = "DPM++ 2M",
                    seed = "123456",
                    subSeed = "",
                    subSeedStrength = "0.0",
                    denoisingStrength = "0.75",
                    hidden = false,
                ),
            ),
        )
    }
}

@Composable
private fun PreviewTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    AiSdAppTheme(
        state = AiSdAppThemeState(
            systemDarkTheme = false,
            darkTheme = darkTheme,
            colorToken = ColorToken.MAUVE,
        ),
        applySystemBars = false,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            content = content,
        )
    }
}

private fun previewTextToImageState() = TextToImageState(
    loadingConfiguration = false,
    onBoardingDemo = true,
    advancedOptionsVisible = true,
    prompt = "portrait photo, soft light, detailed",
    negativePrompt = "low quality, blurry",
    availableSamplers = listOf("DPM++ 2M", "Euler a", "LCM"),
    selectedSampler = "DPM++ 2M",
    batchCount = 2,
)

private fun previewImageToImageState() = ImageToImageState(
    loadingConfiguration = false,
    onBoardingDemo = true,
    prompt = "restore details",
    negativePrompt = "artifacts",
    availableSamplers = StabilityAiSampler.entries.map { "$it" },
    selectedSampler = "${StabilityAiSampler.entries.first()}",
)
