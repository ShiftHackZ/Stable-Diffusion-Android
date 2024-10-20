package com.shifthackz.aisdv1.presentation.screen.onboarding.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.extensions.gesturesDisabled
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.onboarding.buildOnBoardingText
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingDensity
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneAspectRatio
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneWidthFraction
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreenContent
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageState
import com.shifthackz.aisdv1.presentation.widget.dialog.GeneratingProgressDialogContent
import com.shifthackz.aisdv1.presentation.widget.frame.PhoneFrame
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun LocalDiffusionPageContent(
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = buildOnBoardingText(LocalizationR.string.on_boarding_page_local_title),
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight(450),
    )
    Spacer(modifier = Modifier.weight(1f))
    PhoneFrame(
        modifier = Modifier.fillMaxWidth(onBoardingPhoneWidthFraction),
    ) {
        CompositionLocalProvider(LocalDensity provides onBoardingDensity) {
            val localModifier = Modifier
                .aspectRatio(onBoardingPhoneAspectRatio)
            Box(
                contentAlignment = Alignment.Center,
            ) {
                TextToImageScreenContent(
                    modifier = localModifier,
                    state = TextToImageState(
                        onBoardingDemo = true,
                        mode = ServerSource.LOCAL_MICROSOFT_ONNX,
                        advancedToggleButtonVisible = false,
                        advancedOptionsVisible = true,
                        formPromptTaggedInput = true,
                        prompt = "man, photorealistic, black hair, aviator glasses, handsome, beautiful, nature background, <lora:add_detail:1>, <hyper_net:cyberpunk_style_v3:1.5>",
                        negativePrompt = "bad anatomy, bad fingers, distorted, jpeg artifacts",
                        seed = "050598",
                    ),
                )
                Box(
                    modifier = localModifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .gesturesDisabled(),
                    contentAlignment = Alignment.Center,
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        contentAlignment = Alignment.Center,
                    ) {
                        GeneratingProgressDialogContent(
                            titleResId = LocalizationR.string.communicating_local_title,
                            step = 3 to 20,
                        )
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.weight(1f))
}
