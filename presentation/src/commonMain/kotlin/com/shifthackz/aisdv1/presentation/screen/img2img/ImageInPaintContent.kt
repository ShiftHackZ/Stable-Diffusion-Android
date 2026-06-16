@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.theme.global.ApplySystemBarTheme
import com.shifthackz.aisdv1.presentation.theme.global.persistentBottomBarWindowInsets
import com.shifthackz.aisdv1.presentation.theme.global.persistentTopAppBarWindowInsets

/**
 * Renders the `ImageInPaintScreenContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param image image value consumed by the API.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @param onClose callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun ImageInPaintScreenContent(
    modifier: Modifier = Modifier,
    image: ImageBitmap,
    state: ImageInPaintState,
    processIntent: (ImageToImageIntent) -> Unit,
    onClose: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(ImageInPaintTab.Draw) }
    ApplySystemBarTheme(
        colorScheme = MaterialTheme.colorScheme,
        isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f,
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = Localization.string("in_paint_title"))
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                windowInsets = persistentTopAppBarWindowInsets(),
            )
        },
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = selectedTab == ImageInPaintTab.Draw,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
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
                            val enabled = state.strokes.isNotEmpty()
                            OutlinedButton(
                                enabled = enabled,
                                onClick = { processIntent(ImageToImageIntent.UndoInPaintStroke) },
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Undo,
                                    contentDescription = null,
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = Localization.string("action_undo"),
                                    color = LocalContentColor.current,
                                )
                            }
                            OutlinedButton(
                                enabled = enabled,
                                onClick = { processIntent(ImageToImageIntent.ClearInPaintMask) },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CleaningServices,
                                    contentDescription = null,
                                )
                                Text(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    text = Localization.string("action_clear"),
                                    color = LocalContentColor.current,
                                )
                            }
                        }
                        BrushSizeSlider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            size = state.brushSize,
                            onValueChanged = {
                                processIntent(ImageToImageIntent.UpdateInPaintBrushSize(it))
                            },
                        )
                    }
                }
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    windowInsets = persistentBottomBarWindowInsets(),
                ) {
                    ImageInPaintTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            label = {
                                Text(
                                    text = tab.label,
                                    color = LocalContentColor.current,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors().copy(
                                selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                            ),
                            icon = {
                                Icon(
                                    imageVector = when (tab) {
                                        ImageInPaintTab.Draw -> Icons.Default.Image
                                        ImageInPaintTab.Adjust -> Icons.Default.Tune
                                    },
                                    contentDescription = null,
                                )
                            },
                            onClick = { selectedTab = tab },
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            when (selectedTab) {
                ImageInPaintTab.Draw -> ImageInPaintCanvas(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth(),
                    image = image,
                    state = state,
                    onStrokeDrawn = { stroke ->
                        processIntent(ImageToImageIntent.DrawInPaintStroke(stroke))
                    },
                )

                ImageInPaintTab.Adjust -> ImageInPaintParamsForm(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    model = state,
                    processIntent = processIntent,
                )
            }
        }
    }
}
