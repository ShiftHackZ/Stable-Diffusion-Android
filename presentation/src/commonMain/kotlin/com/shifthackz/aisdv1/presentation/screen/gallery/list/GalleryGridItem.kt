@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.extensions.shimmer
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import kotlin.random.Random
import org.koin.core.parameter.parametersOf


/**
 * Renders the `GalleryUiItem` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param item item value consumed by the API.
 * @param checked checked value consumed by the API.
 * @param onClick callback invoked when the user activates the control.
 * @param onLongClick callback invoked by the component.
 * @param onCheckedChange callback invoked by the component.
 * @param selectionMode selection mode value consumed by the API.
 * @author Dmitriy Moroz
 */
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
        if (item.image != null) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = 4.dp,
                        color = borderColor,
                        shape = shape,
                    )
                    .then(
                        if (!item.hidden) Modifier
                        else Modifier.graphicsLayer {
                            renderEffect = BlurEffect(
                                radiusX = 100f,
                                radiusY = 100f,
                            )
                        }
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
                bitmap = item.image,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
        } else {
            Box(
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
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    imageVector = Icons.Default.AutoFixNormal,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
        if (item.hidden) {
            Icon(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.Center),
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
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
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
                ) {
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

/**
 * Renders the `GalleryUiItemShimmer` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
internal fun GalleryUiItemShimmer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .shimmer(),
    )
}

/**
 * Renders the `GalleryEmptyState` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @author Dmitriy Moroz
 */
@Composable
internal fun GalleryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = Localization.string("gallery_empty_title"),
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = Localization.string("gallery_empty_sub_title"),
            textAlign = TextAlign.Center,
        )
    }
}
