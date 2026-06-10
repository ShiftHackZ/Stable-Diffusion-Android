@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.donate

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.extensions.fadedEdge
import com.shifthackz.aisdv1.presentation.widget.item.SupporterItem
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar

data class DonateScreenStrings(
    val title: String,
    val thanksTitle: String,
    val bottomTitle: String,
    val bottomSubtitle: String,
    val bottomEnding: String,
    val backContentDescription: String,
)

@Composable
fun DonateScreenContent(
    state: DonateState,
    strings: DonateScreenStrings,
    processIntent: (DonateIntent) -> Unit,
    modifier: Modifier = Modifier,
    brandIcon: @Composable () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = strings.title,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { processIntent(DonateIntent.NavigateBack) },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = strings.backContentDescription,
                            )
                        },
                    )
                },
            )
        },
        bottomBar = {
            val shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
            )
            val bottomBgColor = MaterialTheme.colorScheme.surface
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
                    .clip(shape)
                    .drawBehind {
                        drawRoundRect(
                            color = bottomBgColor,
                            cornerRadius = CornerRadius(24.dp.toPx()),
                        )
                    }
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    brandIcon()
                    Text(
                        text = strings.bottomTitle,
                        fontWeight = FontWeight.W500,
                        fontSize = 17.sp,
                    )
                }
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = strings.bottomSubtitle,
                    fontWeight = FontWeight.W300,
                    lineHeight = 17.sp,
                )
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = strings.bottomEnding,
                    fontWeight = FontWeight.W300,
                    lineHeight = 17.sp,
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = { processIntent(DonateIntent.LaunchDonate) },
                ) {
                    Text(text = strings.title)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = !state.loading,
            label = "supporters_animation",
        ) { contentVisible ->
            if (contentVisible) {
                if (state.supporters.isNotEmpty()) {
                    val listState = rememberLazyListState()
                    val shadowHeight = 150.dp
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .fadedEdge(gradientOffset = shadowHeight),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScrollbar(listState),
                            state = listState,
                        ) {
                            item {
                                Text(
                                    modifier = Modifier
                                        .padding(
                                            start = 12.dp,
                                            end = 24.dp,
                                            top = 16.dp,
                                            bottom = 4.dp,
                                        ),
                                    text = strings.thanksTitle,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                            items(
                                count = state.supporters.size,
                                key = { index -> state.supporters[index].id },
                            ) { index ->
                                val supporter = state.supporters[index]
                                val itemShape = RoundedCornerShape(12.dp)
                                val bgColor =
                                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f)
                                SupporterItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = if (index == 0) 8.dp else 4.dp,
                                            bottom = 4.dp,
                                        )
                                        .padding(horizontal = 12.dp)
                                        .clip(itemShape)
                                        .drawBehind {
                                            drawRoundRect(
                                                color = bgColor,
                                                cornerRadius = CornerRadius(12.dp.toPx()),
                                            )
                                        },
                                    supporter = supporter,
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(shadowHeight + 32.dp))
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .aspectRatio(1f),
                    )
                }
            }
        }
    }
}
