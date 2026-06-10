package com.shifthackz.aisdv1.presentation.screen.onboarding.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.screen.onboarding.buildOnBoardingText
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingDensity
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneAspectRatio
import com.shifthackz.aisdv1.presentation.screen.onboarding.onBoardingPhoneWidthFraction
import com.shifthackz.aisdv1.presentation.widget.frame.PhoneFrame
import com.shifthackz.aisdv1.presentation.widget.icon.BrandIcons

/**
 * Renders the `UniversalPageContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
fun UniversalPageContent(
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Spacer(modifier = Modifier.weight(1f))
    Text(
        text = buildOnBoardingText(Localization.string("on_boarding_page_universal_title")),
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight(450),
    )
    Spacer(modifier = Modifier.weight(1f))
    PhoneFrame(
        modifier = Modifier.fillMaxWidth(onBoardingPhoneWidthFraction),
    ) {
        CompositionLocalProvider(LocalDensity provides onBoardingDensity) {
            Surface(
                modifier = Modifier.aspectRatio(onBoardingPhoneAspectRatio),
                color = MaterialTheme.colorScheme.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        text = Localization.string("on_boarding_page_universal_subtitle"),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        PlatformCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = Localization.string("on_boarding_page_universal_android"),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(56.dp),
                                    imageVector = Icons.Default.Android,
                                    contentDescription = null,
                                )
                            },
                        )
                        PlatformCard(
                            modifier = Modifier.fillMaxWidth(),
                            title = Localization.string("on_boarding_page_universal_ios"),
                            icon = {
                                Icon(
                                    modifier = Modifier.size(56.dp),
                                    imageVector = BrandIcons.Apple,
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
    Spacer(modifier = Modifier.weight(1f))
}

/**
 * Renders the `PlatformCard` UI for the SDAI presentation layer.
 *
 * @param title title value consumed by the API.
 * @param icon icon value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
private fun PlatformCard(
    title: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(118.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon()
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
