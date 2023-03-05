@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.gallery.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.widget.ZoomableImage
import com.shifthackz.aisdv1.presentation.widget.ZoomableImageSource

class GalleryDetailScreen(
    private val viewModel: GalleryDetailViewModel,
    private val onNavigateBack: () -> Unit = {},
) : MviScreen<GalleryDetailState, GalleryDetailEffect>(viewModel) {

    override val statusBarColor: Color = Color.White
    override val navigationBarColor: Color = Color.Black

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onNavigateBack = onNavigateBack,
        )
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: GalleryDetailState,
    onNavigateBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
//                    Text(stringResource(id = R.string.title_gallery))
                    Text("Details")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        content = {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Back button",
                            )
                        },
                    )
                }
            )
        },
        content = { paddingValues ->
            val contentModifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

            when (state) {
                is GalleryDetailState.Content -> GalleryDetailContentState(
                    modifier = contentModifier,
                    state = state,
                )
                GalleryDetailState.Loading -> Text("Load")
            }
        }
    )
}

@Composable
private fun GalleryDetailContentState(
    modifier: Modifier = Modifier,
    state: GalleryDetailState.Content,
) {
    Column(
        modifier = modifier,
    ) {
        ZoomableImage(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            source = ZoomableImageSource.Bmp(state.bitmap),
        )
    }
}
