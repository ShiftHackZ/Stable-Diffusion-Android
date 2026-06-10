@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
)

package com.shifthackz.aisdv1.presentation.screen.gallery.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import org.koin.core.parameter.parametersOf

/**
 * Renders the `GalleryScreen` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param galleryRouter gallery router value consumed by the API.
 * @param backHandlerEnabled back handler enabled value consumed by the API.
 * @author Dmitriy Moroz
 */
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
