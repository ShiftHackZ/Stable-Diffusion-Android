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

@Composable
fun GalleryScreen(
    modifier: Modifier = Modifier,
    galleryRouter: GalleryRouter? = null,
    backHandlerEnabled: Boolean = true,
) {
    val koin = remember { initKoin() }
    val resolvedGalleryRouter = remember(koin, galleryRouter) {
        galleryRouter ?: koin.get<GalleryRouter>()
    }
    val platformActions = remember(koin) { koin.get<GalleryPlatformActions>() }
    val viewModel = remember(
        koin,
        resolvedGalleryRouter,
    ) {
        koin.get<GalleryViewModel> {
            parametersOf(resolvedGalleryRouter)
        }
    }

    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                is GalleryEffect.OpenMediaStoreFolder -> {
                    platformActions.openMediaStoreFolder(effect.folderUri)
                }
                is GalleryEffect.ShareExport -> {
                    platformActions.shareExport(effect.filePath)
                }
            }
        },
    ) { state, intentHandler ->
        GalleryBackHandler(backHandlerEnabled && state.selectionMode) {
            intentHandler(GalleryIntent.ChangeSelectionMode(false))
        }
        GalleryScreenContent(
            modifier = modifier,
            state = state,
            processIntent = intentHandler,
        )
    }
}
