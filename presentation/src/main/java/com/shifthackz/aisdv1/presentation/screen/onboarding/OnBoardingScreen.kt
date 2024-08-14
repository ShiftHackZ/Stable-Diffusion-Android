@file:OptIn(ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.screen.onboarding.page.FormPageContent
import com.shifthackz.aisdv1.presentation.screen.onboarding.page.LocalDiffusionPageContent
import com.shifthackz.aisdv1.presentation.screen.onboarding.page.LookAndFeelPageContent
import com.shifthackz.aisdv1.presentation.screen.onboarding.page.ProviderPageContent
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnBoardingScreen() {
    MviComponent(
        viewModel = koinViewModel<OnBoardingViewModel>(),
        navigationBarColor = MaterialTheme.colorScheme.surface,
        applySystemUiColors = true,
    ) { state, processIntent ->
        OnBoardingScreenContent(
            processIntent = processIntent,
        )
    }
}

@Composable
private fun OnBoardingScreenContent(
    processIntent: (OnBoardingIntent) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = OnBoardingPage.entries.first().ordinal,
        pageCount = { OnBoardingPage.entries.size },
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            val shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
            )
            Column(
                modifier = Modifier
                    .clip(shape)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
//                Row(
//                    modifier = Modifier.padding(vertical = 12.dp),
//                    horizontalArrangement = Arrangement.spacedBy(4.dp)
//                ) {
//                    repeat(OnBoardingPage.entries.size) { index ->
//                        val selected = pagerState.currentPage == index
//                        Box(
//                            modifier = Modifier
//                                .size(12.dp)
//                                .background(
//                                    MaterialTheme.colorScheme.primary.copy(
//                                        alpha = if (selected) 1f else 0.5f,
//                                    ),
//                                    CircleShape,
//                                )
//                                .clip(CircleShape)
//                        )
//                    }
//                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = {
                            if (pagerState.currentPage == OnBoardingPage.entries.size - 1) {
                                processIntent(OnBoardingIntent.Navigate)
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.DoubleArrow,
                            contentDescription = "Next",
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = true,
            ) { index ->
                when (OnBoardingPage.entries[index]) {
                    OnBoardingPage.Form -> FormPageContent()
                    OnBoardingPage.Providers -> ProviderPageContent()
                    OnBoardingPage.LocalDiffusion -> LocalDiffusionPageContent()
                    OnBoardingPage.LookAndFeel -> LookAndFeelPageContent()
                }
            }
        }
    }
}
