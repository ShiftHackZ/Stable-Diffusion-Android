@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.donate

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.presentation.widget.item.SupporterItem
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.R as PresentationR

@Composable
fun DonateScreen() {
    val context = LocalContext.current
    MviComponent(
        viewModel = koinViewModel<DonateViewModel>(),
        navigationBarColor = MaterialTheme.colorScheme.surface,
        processEffect = { effect ->
            when (effect) {
                is DonateEffect.OpenUrl -> context.openUrl(effect.url)
            }
        }
    ) { state, processIntent ->
        DonateScreenContent(state, processIntent)
    }
}

@Composable
@Preview
private fun DonateScreenContent(
    state: DonateState = DonateState(),
    processIntent: (DonateIntent) -> Unit = {},
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = LocalizationR.string.settings_item_donate),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { processIntent(DonateIntent.NavigateBack) },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back button",
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, shape)
                    .clip(shape)
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Image(
                        modifier = Modifier.size(36.dp),
                        painter = painterResource(id = PresentationR.drawable.ic_sdai_logo),
                        contentDescription = "SDAI Android Branding",
                    )
                    Text(
                        text = stringResource(id = LocalizationR.string.donate_bs_title),
                        fontWeight = FontWeight.W500,
                        fontSize = 17.sp,
                    )
                }
                Text(
                    modifier = Modifier.padding(bottom = 8.dp),
                    text = stringResource(id = LocalizationR.string.donate_bs_sub_title),
                    fontWeight = FontWeight.W300,
                    lineHeight = 17.sp,
                )
                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(id = LocalizationR.string.donate_bs_ending),
                    fontWeight = FontWeight.W300,
                    lineHeight = 17.sp,
                )
                Button(
                    modifier = Modifier
                        .height(height = 60.dp)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    onClick = { processIntent(DonateIntent.LaunchUrl.DonateBmc) },
                    contentPadding = PaddingValues(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffffdd00),
                    ),
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        painter = painterResource(id = PresentationR.drawable.ic_bmc),
                        contentDescription = "Buy me a coffee",
                        contentScale = ContentScale.FillHeight
                    )
                }
            }
        },
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = !state.loading,
            label = "supporters_animation"
        ) { contentVisible ->
            if (contentVisible) {
                if (state.supporters.isNotEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        val shadowHeight = 150.dp
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
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
                                    text = stringResource(id = LocalizationR.string.donate_title_thanks),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                            items(
                                count = state.supporters.size,
                                key = { index -> state.supporters[index].id }
                            ) { index ->
                                val supporter = state.supporters[index]
                                val shape = RoundedCornerShape(12.dp)
                                val bgColor =
                                    MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f)
                                SupporterItem(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .padding(top = if (index == 0) 8.dp else 4.dp)
                                        .padding(bottom = 4.dp)
                                        .fillMaxWidth()
                                        .background(bgColor, shape)
                                        .clip(shape),
                                    supporter = supporter,
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(shadowHeight + 32.dp))
                            }
                        }
                        val shadowGradient = Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background,
                            )
                        )
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(shadowHeight),
                        ) {
                            drawRect(shadowGradient)
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

@Composable
@Preview
private fun StateContentPreview() {
    DonateScreenContent(
        state = DonateState(
            loading = false,
            supporters = listOf(
                Supporter(
                    id = 0,
                    name = "ShiftHackZ",
                    date = Date(),
                    message = "Sdai",
                ),
                Supporter(
                    id = 1,
                    name = "ShiftHackZ",
                    date = Date(),
                    message = "Sdai",
                ),
            ),
        ),
    )
}
