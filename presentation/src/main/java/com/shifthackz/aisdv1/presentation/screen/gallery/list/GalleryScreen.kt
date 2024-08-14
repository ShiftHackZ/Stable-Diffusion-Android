@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class,
)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import android.content.Intent
import android.provider.DocumentsContract
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.extensions.items
import com.shifthackz.aisdv1.core.extensions.shake
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.sharing.shareFile
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.random.Random
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun GalleryScreen() {
    val viewModel = koinViewModel<GalleryViewModel>()
    val context = LocalContext.current
    val fileProviderDescriptor: FileProviderDescriptor = koinInject()
    val pagingFlow = viewModel.pagingFlow
    val lazyGalleryItems = pagingFlow.collectAsLazyPagingItems()
    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                is GalleryEffect.OpenUri -> with(Intent(Intent.ACTION_VIEW)) {
                    setDataAndType(effect.uri, DocumentsContract.Document.MIME_TYPE_DIR)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(this)
                }

                is GalleryEffect.Share -> context.shareFile(
                    file = effect.zipFile,
                    fileProviderPath = fileProviderDescriptor.providerPath,
                    fileMimeType = Constants.MIME_TYPE_ZIP,
                )

                GalleryEffect.Refresh -> {
                    lazyGalleryItems.refresh()
                }
            }
        },
        applySystemUiColors = false,
    ) { state, intentHandler ->
        BackHandler(state.selectionMode) {
            intentHandler(GalleryIntent.ChangeSelectionMode(false))
        }
        GalleryScreenContent(
            state = state,
            lazyGalleryItems = lazyGalleryItems,
            processIntent = intentHandler,
        )
    }
}

@Composable
fun GalleryScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryState,
    lazyGalleryItems: LazyPagingItems<GalleryGridItemUi>,
    processIntent: (GalleryIntent) -> Unit = {},
) {
    val listState = rememberLazyGridState()

    val emptyStatePredicate: () -> Boolean = {
        lazyGalleryItems.loadState.refresh is LoadState.NotLoading
                && lazyGalleryItems.itemCount == 0
                && lazyGalleryItems.loadState.append.endOfPaginationReached
    }

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
                                            GalleryIntent.Drawer(DrawerIntent.Open)
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
                                        contentDescription = if (isInSelectionMode) "Close" else "Menu",
                                    )
                                }
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(id = LocalizationR.string.title_gallery),
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
                                                    contentDescription = "Delete",
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    processIntent(GalleryIntent.Export.Selection.Request)
                                                },
                                            ) {
                                                Image(
                                                    modifier = Modifier.size(24.dp),
                                                    painter = painterResource(id = R.drawable.ic_share),
                                                    contentDescription = "Export",
                                                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    AnimatedVisibility(
                                        visible = lazyGalleryItems.itemCount != 0,
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
                                                contentDescription = "Dropdown",
                                            )
                                        }
                                    }
                                }
                            }
                            DropdownMenu(
                                expanded = state.dropdownMenuShow,
                                onDismissRequest = { processIntent(GalleryIntent.Dropdown.Close) },
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Checklist,
                                            contentDescription = "Dropdown",
                                            tint = LocalContentColor.current,
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(
                                                id = LocalizationR.string.gallery_menu_selection_mode,
                                            ),
                                        )
                                    },
                                    onClick = {
                                        processIntent(GalleryIntent.Dropdown.Close)
                                        processIntent(GalleryIntent.ChangeSelectionMode(true))
                                    },
                                )
                                if (state.mediaStoreInfo.isNotEmpty) DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.FileOpen,
                                            contentDescription = "Browse",
                                            tint = LocalContentColor.current,
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(id = LocalizationR.string.browse)
                                        )
                                    },
                                    onClick = {
                                        processIntent(GalleryIntent.Dropdown.Close)
                                        state.mediaStoreInfo.folderUri?.let {
                                            processIntent(GalleryIntent.OpenMediaStoreFolder(it))
                                        }
                                    },
                                )
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Image(
                                            modifier = Modifier.size(24.dp),
                                            painter = painterResource(id = R.drawable.ic_share),
                                            contentDescription = "Export",
                                            colorFilter = ColorFilter.tint(LocalContentColor.current),
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(id = LocalizationR.string.gallery_menu_export_all)
                                        )
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
                                            contentDescription = "Delete",
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = stringResource(id = LocalizationR.string.gallery_menu_delete_all),
                                        )
                                    },
                                    onClick = {
                                        processIntent(GalleryIntent.Dropdown.Close)
                                        processIntent(GalleryIntent.Delete.All.Request)
                                    },
                                )
                            }
                        },
                    )
                    BackgroundWorkWidget(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 4.dp),
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = state.selectionMode,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceTint),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp),
                            text = stringResource(
                                id = LocalizationR.string.gallery_menu_selected,
                                "${state.selection.size}",
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.W400,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            modifier = Modifier.padding(end = 16.dp),
                            onClick = {
                                if (state.selection.isNotEmpty()) {
                                    processIntent(GalleryIntent.UnselectAll)
                                } else {
                                    processIntent(GalleryIntent.ChangeSelectionMode(false))
                                }
                            },
                        ) {
                            val resId = if (state.selection.isNotEmpty()) {
                                LocalizationR.string.gallery_menu_unselect_all
                            } else {
                                LocalizationR.string.cancel
                            }
                            Text(
                                text = stringResource(resId).toUpperCase(Locale.current),
                                textAlign = TextAlign.Center,
                                color = LocalContentColor.current,
                            )
                        }
                    }
                }
                AnimatedVisibility(
                    visible = state.mediaStoreInfo.isNotEmpty && !state.selectionMode,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceTint),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 16.dp)
                                .fillMaxWidth(0.65f),
                            text = stringResource(
                                id = LocalizationR.string.gallery_media_store_banner,
                                "${state.mediaStoreInfo.count}",
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.W400,
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            modifier = Modifier.padding(end = 16.dp),
                            onClick = {
                                state.mediaStoreInfo.folderUri?.let {
                                    processIntent(GalleryIntent.OpenMediaStoreFolder(it))
                                }
                            },
                        ) {
                            Text(
                                text = stringResource(id = LocalizationR.string.browse)
                                    .toUpperCase(Locale.current),
                                color = LocalContentColor.current,
                            )
                        }
                    }
                }
            },
        ) { paddingValues ->
            when {
                emptyStatePredicate() -> GalleryEmptyState(Modifier.fillMaxSize())

                lazyGalleryItems.itemCount == 0 -> LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    columns = GridCells.Fixed(state.grid.size),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    val max = when (state.grid) {
                        Grid.Fixed2 -> 6
                        Grid.Fixed3 -> 12
                        Grid.Fixed4 -> 20
                        Grid.Fixed5 -> 30
                    }
                    repeat(max) {
                        item(it) {
                            GalleryUiItemShimmer()
                        }
                    }
                }

                else -> LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    columns = GridCells.Fixed(state.grid.size),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    state = listState,
                ) {
                    items(lazyGalleryItems) { item ->
                        if (item != null) {
                            val selected = state.selection.contains(item.id)
                            GalleryUiItem(
                                modifier = Modifier
                                    .animateItemPlacement(tween(500))
                                    .shake(
                                        enabled = state.selectionMode && !selected,
                                        animationDurationMillis = 188,
                                        animationStartOffset = Random.nextInt(0, 320),
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
                                    processIntent(GalleryIntent.OpenItem(it))
                                },
                            )
                        } else {
                            GalleryUiItemShimmer()
                        }
                    }
                }
            }
        }
        ModalRenderer(screenModal = state.screenModal) {
            (it as? GalleryIntent)?.let(processIntent::invoke)
        }
    }
}

@Composable
fun GalleryUiItem(
    modifier: Modifier = Modifier,
    item: GalleryGridItemUi,
    checked: Boolean = false,
    onClick: (GalleryGridItemUi) -> Unit = {},
    onLongClick: () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {},
    selectionMode: Boolean = false,
) {
    val shape = RoundedCornerShape(12.dp)
    val borderColor by animateColorAsState(
        targetValue = if (selectionMode && checked) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        label = "border_color",
    )
    Box(
        modifier = modifier.clip(shape),
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(
                    width = 4.dp,
                    color = borderColor,
                    shape = shape,
                )
                .combinedClickable(
                    onLongClick = if (!selectionMode) onLongClick else null,
                    onClick = {
                        if (!selectionMode) {
                            onClick(item)
                        } else {
                            onCheckedChange(!checked)
                        }
                    },
                ),
            bitmap = item.bitmap.asImageBitmap(),
            contentScale = ContentScale.Crop,
            contentDescription = "gallery_item",
        )
        if (selectionMode) {
            val checkBoxShape = RoundedCornerShape(4.dp)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .clip(checkBoxShape)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        shape = checkBoxShape,
                    ),
            ) {
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryUiItemShimmer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .shimmer()
    )
}

@Composable
private fun GalleryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = LocalizationR.string.gallery_empty_title),
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = LocalizationR.string.gallery_empty_sub_title),
            textAlign = TextAlign.Center,
        )
    }
}
