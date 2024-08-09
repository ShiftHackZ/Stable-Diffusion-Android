@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.inpaint

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.model.Modal
import com.shifthackz.aisdv1.presentation.screen.inpaint.components.CapSizeSlider
import com.shifthackz.aisdv1.presentation.screen.inpaint.forms.ImageDrawForm
import com.shifthackz.aisdv1.presentation.screen.inpaint.forms.InPaintParamsForm
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun InPaintScreen(
    modifier: Modifier = Modifier,
) {
    MviComponent(
        viewModel = koinViewModel<InPaintViewModel>(),
        applySystemUiColors = true,
        navigationBarColor = MaterialTheme.colorScheme.surface,
    ) { state, processIntent ->
        ScreenContent(
            modifier = modifier,
            state = state,
            processIntent = processIntent,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: InPaintState,
    processIntent: (InPaintIntent) -> Unit = {},
) {
    BackHandler {
        processIntent(InPaintIntent.NavigateBack)
    }
    Box(modifier) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = LocalizationR.string.in_paint_title),
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { processIntent(InPaintIntent.NavigateBack) },
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
                Column {
                    AnimatedVisibility(
                        visible = state.selectedTab == InPaintState.Tab.IMAGE,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface),
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround,
                            ) {
                                val isEnabled = state.model.paths.isNotEmpty()
                                OutlinedButton(
                                    onClick = { processIntent(InPaintIntent.Action.Undo) },
                                    enabled = isEnabled,
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Undo,
                                        contentDescription = "Undo",
                                    )
                                    Text(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        text = stringResource(id = LocalizationR.string.action_undo),
                                        color = LocalContentColor.current,
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        processIntent(InPaintIntent.ScreenModal.Show(Modal.ClearInPaintConfirm))
                                    },
                                    enabled = isEnabled,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CleaningServices,
                                        contentDescription = "Clear",
                                    )
                                    Text(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        text = stringResource(id = LocalizationR.string.action_clear),
                                        color = LocalContentColor.current,
                                    )
                                }
                            }
                            CapSizeSlider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                size = state.size,
                                onValueChanged = { processIntent(InPaintIntent.ChangeCapSize(it)) },
                            )
                        }
                    }
                    NavigationBar {
                        InPaintState.Tab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = state.selectedTab == tab,
                                label = {
                                    Text(
                                        text = stringResource(id = tab.label),
                                        color = LocalContentColor.current,
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors().copy(
                                    selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                                ),
                                icon = {
                                    Image(
                                        modifier = Modifier.size(24.dp),
                                        painter = painterResource(tab.iconRes),
                                        contentDescription = stringResource(id = LocalizationR.string.gallery_tab_image),
                                        colorFilter = ColorFilter.tint(LocalContentColor.current),
                                    )
                                },
                                onClick = { processIntent(InPaintIntent.SelectTab(tab)) },
                            )
                        }
                    }
                }
            },
        ) { paddingValues ->
            val pagerState = rememberPagerState(
                initialPage = 0,
                pageCount = { InPaintState.Tab.entries.size },
            )
            VerticalPager(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight(),
                state = pagerState,
                userScrollEnabled = false,
            ) { index ->
                when (InPaintState.Tab.entries[index]) {
                    InPaintState.Tab.IMAGE -> ImageDrawForm(
                        state = state,
                        processIntent = processIntent,
                    )

                    InPaintState.Tab.FORM -> InPaintParamsForm(
                        modifier = Modifier.padding(16.dp),
                        model = state.model,
                        processIntent = processIntent,
                    )
                }
            }
            LaunchedEffect(state.selectedTab) {
                pagerState.animateScrollToPage(state.selectedTab.ordinal)
            }
        }
        ModalRenderer(screenModal = state.screenModal) {
            (it as? InPaintIntent)?.let(processIntent::invoke)
        }
    }
}
