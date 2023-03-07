package com.shifthackz.aisdv1.presentation.screen.img2img

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.shifthackz.aisdv1.core.ui.MviScreen

class ImageToImageScreen(
    private val viewModel: ImageToImageViewModel,
) : MviScreen<ImageToImageState, ImageToImageEffect>(viewModel) {

    @Composable
    override fun Content() {

    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ImageToImageState,
) {

}
