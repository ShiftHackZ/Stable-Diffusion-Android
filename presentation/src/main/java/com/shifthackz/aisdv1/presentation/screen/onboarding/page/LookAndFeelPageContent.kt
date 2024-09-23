package com.shifthackz.aisdv1.presentation.screen.onboarding.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.extensions.EmptyLambda
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.screen.onboarding.buildOnBoardingText
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingDensity
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneAspectRatio
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneWidthFraction
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsScreenContent
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsState
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppTheme
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppThemeState
import com.shifthackz.aisdv1.presentation.theme.isSdAppInDarkTheme
import com.shifthackz.aisdv1.presentation.widget.frame.PhoneFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun LookAndFeelPageContent(
    modifier: Modifier = Modifier,
    darkThemeToken: DarkThemeToken,
    appVersion: String,
    isPageVisible: Boolean = false,
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    val scope = rememberCoroutineScope()
    val darkTheme = isSdAppInDarkTheme()
    var themeState by remember {
        mutableStateOf(
            AiSdAppThemeState(
                systemColorPalette = false,
                systemDarkTheme = false,
                darkTheme = darkTheme,
                darkThemeToken = darkThemeToken,
            ),
        )
    }
    var settingsState by remember {
        mutableStateOf(
            SettingsState(
                loading = false,
                onBoardingDemo = true,
                colorToken = themeState.colorToken,
                darkThemeToken = themeState.darkThemeToken,
                darkTheme = darkTheme,
                appVersion = appVersion
            ),
        )
    }
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = buildOnBoardingText(LocalizationR.string.on_boarding_page_ui_title),
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight(450),
    )
    Spacer(modifier = Modifier.weight(1f))
    PhoneFrame(
        modifier = Modifier.fillMaxWidth(onBoardingPhoneWidthFraction),
    ) {
        CompositionLocalProvider(LocalDensity provides onBoardingDensity) {
            AiSdAppTheme(themeState) {
                SettingsScreenContent(
                    modifier = Modifier.aspectRatio(onBoardingPhoneAspectRatio),
                    state = settingsState,
                )
                Box(
                    modifier = Modifier
                        .aspectRatio(onBoardingPhoneAspectRatio)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = EmptyLambda
                        ),
                )
            }
        }
    }
    DisposableEffect(isPageVisible) {
        val job = scope.launch {
            while (isPageVisible) {
                delay(700)
                val colorToken = ColorToken.entries.random()
                settingsState = settingsState.copy(
                    galleryGrid = Grid.entries.random(),
                    colorToken = colorToken,
                )
                themeState = themeState.copy(
                    colorToken = colorToken,
                )
            }
        }
        onDispose {
            job.cancel()
        }
    }
    Spacer(modifier = Modifier.weight(1f))
}
