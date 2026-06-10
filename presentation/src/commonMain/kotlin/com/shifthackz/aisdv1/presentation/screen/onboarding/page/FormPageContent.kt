package com.shifthackz.aisdv1.presentation.screen.onboarding.page

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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.extensions.gesturesDisabled
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.screen.onboarding.buildOnBoardingText
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingDensity
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneAspectRatio
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneWidthFraction
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageContent
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageState
import com.shifthackz.aisdv1.presentation.widget.frame.PhoneFrame

/**
 * Renders the `FormPageContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param isPageVisible is page visible value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun FormPageContent(
    modifier: Modifier = Modifier,
    isPageVisible: Boolean = false,
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = buildOnBoardingText(Localization.string("on_boarding_page_form_title")),
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
    )
    Spacer(modifier = Modifier.weight(1f))
    PhoneFrame(
        modifier = Modifier.fillMaxWidth(onBoardingPhoneWidthFraction),
    ) {
        CompositionLocalProvider(LocalDensity provides onBoardingDensity) {
            TextToImageContent(
                modifier = Modifier.aspectRatio(onBoardingPhoneAspectRatio),
                state = TextToImageState(
                    loadingConfiguration = false,
                    onBoardingDemo = true,
                    formPromptTaggedInput = true,
                    prompt = "man, photorealistic, black hair, aviator glasses, handsome, beautiful, nature background",
                    negativePrompt = "bad anatomy, bad fingers, distorted, jpeg artifacts",
                    width = "512",
                    height = "512",
                    samplingSteps = 24,
                    cfgScale = 7.5f,
                    batchCount = 2,
                ),
                processIntent = {},
            )
            Box(
                modifier = Modifier
                    .aspectRatio(onBoardingPhoneAspectRatio)
                    .gesturesDisabled(),
            )
        }
    }
    Spacer(modifier = Modifier.weight(1f))
}
