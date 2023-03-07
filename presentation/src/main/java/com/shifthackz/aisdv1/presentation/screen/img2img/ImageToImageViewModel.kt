package com.shifthackz.aisdv1.presentation.screen.img2img

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.viewmodel.MviViewModel

class ImageToImageViewModel(
    private val schedulersProvider: SchedulersProvider,
) : MviViewModel<ImageToImageState, ImageToImageEffect>() {

    override val emptyState = ImageToImageState()

}
