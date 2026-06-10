@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import org.koin.core.parameter.parametersOf

/**
 * Renders the `GalleryDetailScreen` UI for the SDAI presentation layer.
 *
 * @param itemId item id value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param router router value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun GalleryDetailScreen(
    itemId: Long,
    modifier: Modifier = Modifier,
    router: GalleryDetailRouter? = null,
) {
    val koin = remember { initKoin() }
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<GalleryDetailRouter>()
    }
    val platformActions = remember(koin) { koin.get<GalleryDetailPlatformActions>() }
    val viewModel = remember(
        koin,
        itemId,
        resolvedRouter,
        platformActions,
    ) {
        koin.get<GalleryDetailViewModel> {
            parametersOf(itemId, resolvedRouter, platformActions)
        }
    }

    MviComponent(viewModel = viewModel) { state, intentHandler ->
        GalleryDetailScreenContent(
            modifier = modifier.fillMaxSize(),
            state = state,
            processIntent = intentHandler,
        )
    }
}
