@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import kotlin.random.Random


/**
 * Renders the `GalleryScreenContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun GalleryScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryState,
    processIntent: (GalleryIntent) -> Unit = {},
) {
    val listState = rememberLazyGridState()

    Box(modifier) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            AnimatedContent(
                                targetState = state.selectionMode,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "main_nav_icon_animation",
                            ) { isInSelectionMode ->
                                IconButton(
                                    onClick = {
                                        val intent = if (isInSelectionMode) {
                                            GalleryIntent.ChangeSelectionMode(false)
                                        } else {
                                            GalleryIntent.OpenDrawer
                                        }
                                        processIntent(intent)
                                    },
                                ) {
                                    Icon(
                                        imageVector = if (isInSelectionMode) {
                                            Icons.Default.Close
                                        } else {
                                            Icons.Default.Menu
                                        },
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        title = {
                            Text(
                                text = Localization.string("title_gallery"),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        actions = {
                            AnimatedContent(
                                targetState = state.selectionMode,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "action_nav_icon_animation",
                            ) { isInSelectionMode ->
                                if (isInSelectionMode) {
                                    AnimatedVisibility(
                                        visible = state.selection.isNotEmpty(),
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ) {
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    processIntent(GalleryIntent.Delete.Selection.Request)
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null,
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    processIntent(GalleryIntent.Export.Selection.Request)
                                                },
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = null,
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    AnimatedVisibility(
                                        visible = state.items.isNotEmpty(),
                                        enter = fadeIn(),
                                        exit = fadeOut(),
                                    ) {
                                        IconButton(
                                            onClick = {
                                                processIntent(GalleryIntent.Dropdown.Toggle)
                                            },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                }
                            }
                            DropdownMenu(
                                expanded = state.dropdownMenuShow,
                                onDismissRequest = { processIntent(GalleryIntent.Dropdown.Close) },
                                containerColor = MaterialTheme.colorScheme.background,
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Checklist,
                                            contentDescription = null,
                                            tint = LocalContentColor.current,
                                        )
                                    },
                                    text = {
                                        Text(text = Localization.string("gallery_menu_selection_mode"))
                                    },
                                    onClick = {
                                        processIntent(GalleryIntent.Dropdown.Close)
                                        processIntent(GalleryIntent.ChangeSelectionMode(true))
                                    },
                                )
                                if (state.mediaStoreInfo.isNotEmpty) {
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.FileOpen,
                                                contentDescription = null,
                                                tint = LocalContentColor.current,
                                            )
                                        },
                                        text = {
                                            Text(text = Localization.string("browse"))
                                        },
                                        onClick = {
                                            processIntent(GalleryIntent.Dropdown.Close)
                                            processIntent(GalleryIntent.OpenMediaStoreFolder)
                                        },
                                    )
                                }
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = null,
                                            tint = LocalContentColor.current,
                                        )
                                    },
                                    text = {
                                        Text(text = Localization.string("gallery_menu_export_all"))
                                    },
                                    onClick = {
                                        processIntent(GalleryIntent.Dropdown.Close)
                                        processIntent(GalleryIntent.Export.All.Request)
                                    },
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = null,
                                        )
                                    },
                                    text = {
                                        Text(text = Localization.string("gallery_menu_delete_all"))
                                    },
                                    onClick = {
                                        processIntent(GalleryIntent.Dropdown.Close)
                                        processIntent(GalleryIntent.Delete.All.Request)
                                    },
                                )
                            }
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0),
                    )
                    BackgroundWorkWidget(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 4.dp),
                    )
                }
            },
            bottomBar = {
                GalleryBottomBars(
                    state = state,
                    processIntent = processIntent,
                )
            },
        ) { paddingValues ->
            when {
                state.loading -> GalleryLoadingGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    grid = state.grid,
                )
                state.items.isEmpty() -> GalleryEmptyState(Modifier.fillMaxSize())
                else -> LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())
                        .verticalScrollbar(listState),
                    columns = GridCells.Fixed(state.grid.size),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = listState,
                ) {
                    items(
                        items = state.items,
                        key = GalleryGridItemUi::id,
                    ) { item ->
                        val selected = state.selection.contains(item.id)
                        GalleryUiItem(
                            modifier = Modifier
                                .selectionJiggle(
                                    enabled = state.selectionMode && !selected,
                                    animationStartOffset = remember(item.id) {
                                        Random.nextInt(0, 320)
                                    },
                                ),
                            item = item,
                            selectionMode = state.selectionMode,
                            checked = selected,
                            onCheckedChange = {
                                processIntent(GalleryIntent.ToggleItemSelection(item.id))
                            },
                            onLongClick = {
                                processIntent(GalleryIntent.ChangeSelectionMode(true))
                                processIntent(GalleryIntent.ToggleItemSelection(item.id))
                            },
                            onClick = {
                                processIntent(GalleryIntent.OpenItem(it.id))
                            },
                        )
                    }
                    if (state.canLoadMore) {
                        item(key = "load_next_page") {
                            LaunchedEffect(state.nextPage) {
                                processIntent(GalleryIntent.LoadNextPage)
                            }
                            GalleryUiItemShimmer()
                        }
                    }
                    items(2, key = { "bottom_spacer_$it" }) {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
        GalleryDialogRenderer(
            dialog = state.dialog,
            processIntent = processIntent,
        )
    }
}
