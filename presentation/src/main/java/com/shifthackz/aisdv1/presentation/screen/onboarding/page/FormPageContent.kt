package com.shifthackz.aisdv1.presentation.screen.onboarding.page

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.extensions.gesturesDisabled
import com.shifthackz.aisdv1.presentation.screen.onboarding.buildOnBoardingText
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingDensity
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneAspectRatio
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneWidthFraction
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreenContent
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageState
import com.shifthackz.aisdv1.presentation.widget.frame.PhoneFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun FormPageContent(
    modifier: Modifier = Modifier,
    isPageVisible: Boolean = false,
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = buildOnBoardingText(LocalizationR.string.on_boarding_page_form_title),
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.weight(1f))
    PhoneFrame(
        modifier = Modifier.fillMaxWidth(onBoardingPhoneWidthFraction),
    ) {
        CompositionLocalProvider(LocalDensity provides onBoardingDensity) {
            TextToImageScreenContent(
                modifier = Modifier.aspectRatio(onBoardingPhoneAspectRatio),
                state = TextToImageState(
                    onBoardingDemo = true,
                    advancedToggleButtonVisible = false,
                    advancedOptionsVisible = true,
                    formPromptTaggedInput = true,
                    prompt = "man, photorealistic, black hair, aviator glasses, handsome, beautiful, nature background, <lora:add_detail:1>, <hyper_net:cyberpunk_style_v3:1.5>",
                    negativePrompt = "bad anatomy, bad fingers, distorted, jpeg artifacts",
                    selectedSampler = "DPM++ 2M",
                    availableSamplers = listOf("DPM++ 2M"),
                    seed = "050598",
                    subSeed = "151297",
                    subSeedStrength = 0.69f,
                ),
                scrollState = scrollState,
            )
            Box(
                modifier = Modifier
                    .aspectRatio(onBoardingPhoneAspectRatio)
                    .gesturesDisabled(),
            )
        }
    }
    Spacer(modifier = Modifier.weight(1f))
    DisposableEffect(isPageVisible) {
        val job = scope.launch {
            while (isPageVisible) {
                scrollState.scrollTo(0)
                delay(2000)
                scrollState.animateScrollTo(scrollState.maxValue / 2 + 60, tween(2000))
                delay(1000)
                scrollState.animateScrollTo(scrollState.maxValue, tween(2000))
                delay(1000)
            }
        }
        onDispose {
            job.cancel()
        }
    }
}
