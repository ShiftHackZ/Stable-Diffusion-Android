@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog


/**
 * Renders the `selectionJiggle` UI for the SDAI presentation layer.
 *
 * @param enabled enabled value consumed by the API.
 * @param animationStartOffset animation start offset value consumed by the API.
 * @return Result produced by `selectionJiggle`.
 * @author Dmitriy Moroz
 */
@Composable
internal fun Modifier.selectionJiggle(
    enabled: Boolean,
    animationStartOffset: Int,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "selection_jiggle")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = .99f,
        animationSpec = infiniteRepeatable(
            animation = tween(188, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(animationStartOffset),
        ),
        label = "selection_jiggle_scale",
    )
    val rotation by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(188, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
            initialStartOffset = StartOffset(animationStartOffset),
        ),
        label = "selection_jiggle_rotation",
    )
    return graphicsLayer {
        scaleX = if (enabled) scale else 1f
        scaleY = if (enabled) scale else 1f
        rotationZ = if (enabled) rotation else 0f
    }
}

/**
 * Renders the `GalleryBottomBars` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GalleryBottomBars(
    state: GalleryState,
    processIntent: (GalleryIntent) -> Unit,
) {
    AnimatedVisibility(
        visible = state.selectionMode,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column {
            AnimatedVisibility(
                visible = state.selection.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                GallerySelectionActionsBar(
                    state = state,
                    processIntent = processIntent,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceTint),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = Localization.string("gallery_menu_selected", "${state.selection.size}"),
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
                    val text = if (state.selection.isNotEmpty()) {
                        Localization.string("gallery_menu_unselect_all")
                    } else {
                        Localization.string("cancel")
                    }
                    Text(
                        text = text.uppercase(),
                        textAlign = TextAlign.Center,
                        color = LocalContentColor.current,
                    )
                }
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
                text = Localization.string("gallery_media_store_banner", "${state.mediaStoreInfo.count}"),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 17.sp,
                fontWeight = FontWeight.W400,
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                modifier = Modifier.padding(end = 16.dp),
                onClick = {
                    processIntent(GalleryIntent.OpenMediaStoreFolder)
                },
            ) {
                Text(
                    text = Localization.string("browse").uppercase(),
                    color = LocalContentColor.current,
                )
            }
        }
    }
}

/**
 * Renders selected gallery item actions above the selected-images summary bar.
 *
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun GallerySelectionActionsBar(
    state: GalleryState,
    processIntent: (GalleryIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceTint)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                processIntent(GalleryIntent.ToggleSelectionLike)
            },
        ) {
            Icon(
                imageVector = if (state.shouldLikeSelection) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = null,
            )
        }
        IconButton(
            onClick = {
                processIntent(GalleryIntent.ToggleSelectionVisibility)
            },
        ) {
            Icon(
                imageVector = if (state.shouldHideSelection) {
                    Icons.Default.VisibilityOff
                } else {
                    Icons.Default.Visibility
                },
                contentDescription = null,
            )
        }
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

/**
 * Renders the `GalleryDialogRenderer` UI for the SDAI presentation layer.
 *
 * @param dialog dialog value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GalleryDialogRenderer(
    dialog: GalleryDialog,
    processIntent: (GalleryIntent) -> Unit,
) {
    when (dialog) {
        GalleryDialog.None -> Unit
        GalleryDialog.DeleteAllConfirm -> DeleteGalleryDialog(
            titleKey = "interaction_delete_all_title",
            textKey = "interaction_delete_all_sub_title",
            onConfirm = { processIntent(GalleryIntent.Delete.All.Confirm) },
            onDismiss = { processIntent(GalleryIntent.DismissDialog) },
        )
        GalleryDialog.DeleteSelectionConfirm -> DeleteGalleryDialog(
            titleKey = "interaction_delete_selection_title",
            textKey = "interaction_delete_selection_sub_title",
            onConfirm = { processIntent(GalleryIntent.Delete.Selection.Confirm) },
            onDismiss = { processIntent(GalleryIntent.DismissDialog) },
        )
        is GalleryDialog.ConfirmExport -> DecisionInteractiveDialog(
            title = Localization.string("interaction_export_title").asUiText(),
            text = Localization.string(
                if (dialog.exportAll) {
                    "interaction_export_sub_title"
                } else {
                    "interaction_export_sub_title_selection"
                }
            ).asUiText(),
            confirmActionText = Localization.string("action_export").asUiText(),
            onConfirmAction = {
                processIntent(
                    if (dialog.exportAll) {
                        GalleryIntent.Export.All.Confirm
                    } else {
                        GalleryIntent.Export.Selection.Confirm
                    }
                )
            },
            onDismissRequest = { processIntent(GalleryIntent.DismissDialog) },
        )
        GalleryDialog.ExportInProgress -> ProgressDialog(
            title = Localization.string("exporting_progress_title").asUiText(),
            subTitle = Localization.string("exporting_progress_sub_title").asUiText(),
            canDismiss = false,
        )
        is GalleryDialog.Error -> ErrorDialog(
            text = dialog.message.asUiText(),
            onDismissRequest = { processIntent(GalleryIntent.DismissDialog) },
        )
    }
}

/**
 * Renders the `DeleteGalleryDialog` UI for the SDAI presentation layer.
 *
 * @param titleKey title key value consumed by the API.
 * @param textKey text key value consumed by the API.
 * @param onConfirm callback invoked by the component.
 * @param onDismiss callback invoked when the UI should be dismissed.
 * @author Dmitriy Moroz
 */
@Composable
internal fun DeleteGalleryDialog(
    titleKey: String,
    textKey: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    DecisionInteractiveDialog(
        title = Localization.string(titleKey).asUiText(),
        text = Localization.string(textKey).asUiText(),
        confirmActionText = Localization.string("yes").asUiText(),
        dismissActionText = Localization.string("no").asUiText(),
        onConfirmAction = onConfirm,
        onDismissRequest = onDismiss,
    )
}

/**
 * Renders the `GalleryLoadingGrid` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param grid grid value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GalleryLoadingGrid(
    modifier: Modifier,
    grid: Grid,
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(grid.size),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        galleryShimmerItems(grid)
    }
}

/**
 * Executes the `galleryShimmerItems` step in the SDAI presentation layer.
 *
 * @param grid grid value consumed by the API.
 * @author Dmitriy Moroz
 */
internal fun LazyGridScope.galleryShimmerItems(grid: Grid) {
    val max = when (grid) {
        Grid.Fixed2 -> 6
        Grid.Fixed3 -> 12
        Grid.Fixed4 -> 20
        Grid.Fixed5 -> 30
    }
    repeat(max) { index ->
        item(key = "shimmer_$index") {
            GalleryUiItemShimmer()
        }
    }
}
