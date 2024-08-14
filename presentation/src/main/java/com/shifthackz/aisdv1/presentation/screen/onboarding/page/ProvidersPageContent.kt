package com.shifthackz.aisdv1.presentation.screen.onboarding.page

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
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.extensions.gesturesDisabled
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupScreenContent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.widget.frame.PhoneFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProviderPageContent(
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    val scope = rememberCoroutineScope()
    var serverState by remember {
        mutableStateOf(
            ServerSetupState(
                showBackNavArrow = false,
            )
        )
    }
    Spacer(modifier = Modifier.weight(2f))
    Text(
        text = "Freedom to choose your AI generation provider.",
        fontSize = 24.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight(450),
    )
    Spacer(modifier = Modifier.weight(2f))
    PhoneFrame(
        modifier = Modifier
            .fillMaxWidth(0.74f),
    ) {
        CompositionLocalProvider(
            LocalDensity provides Density(2.15f, 1f),
        ) {
            ServerSetupScreenContent(
                modifier = Modifier
                    .gesturesDisabled()
                    .aspectRatio(9 / 16f),
                state = serverState,
            )
        }
    }
    Spacer(modifier = Modifier.weight(1f))
    DisposableEffect(Unit) {
        val job = scope.launch {
            while (true) {
                delay(700)
                serverState = serverState.copy(
                    mode = ServerSource.entries.random(),
                )
            }
        }
        onDispose {
            job.cancel()
        }
    }
}
