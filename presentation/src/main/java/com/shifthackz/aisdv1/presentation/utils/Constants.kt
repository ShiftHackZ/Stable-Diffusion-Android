package com.shifthackz.aisdv1.presentation.utils

import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute.HomeNavigation

object Constants {
    const val PAGINATION_PAYLOAD_SIZE = 1000
    const val DEBUG_MENU_ACCESS_TAPS = 7

    const val SUB_SEED_STRENGTH_MIN = 0f
    const val SUB_SEED_STRENGTH_MAX = 1f

    const val SAMPLING_STEPS_RANGE_MIN = 1
    const val SAMPLING_STEPS_RANGE_MAX = 150
    const val SAMPLING_STEPS_RANGE_STABILITY_AI_MAX = 50
    const val SAMPLING_STEPS_LOCAL_DIFFUSION_MAX = 50

    const val BATCH_RANGE_MIN = 1
    const val BATCH_RANGE_MAX = 20

    const val CFG_SCALE_RANGE_MIN = 1
    const val CFG_SCALE_RANGE_MAX = 35

    const val DENOISING_STRENGTH_MIN = 0f
    const val DENOISING_STRENGTH_MAX = 1f

    const val DRAW_CAP_RANGE_MIN = 1
    const val DRAW_CAP_RANGE_MAX = 60

    const val MASK_BLUR_MIN = 1
    const val MASK_BLUR_MAX = 64

    const val ONLY_MASKED_PADDING_MIN = 0
    const val ONLY_MASKED_PADDING_MAX = 256

    const val EXTRA_MINIMUM = -10.0
    const val EXTRA_MAXIMUM = 10.0
    const val EXTRA_STEP = 0.25

    const val MIME_TYPE_ZIP = "application/zip"
    const val MIME_TYPE_JPG = "image/jpeg"

    const val HORDE_DEFAULT_API_KEY = "0000000000"

    val sizes = listOf("64", "128", "256", "320", "384", "448", "512")

    val homeRoutes = listOf(
        HomeNavigation.TxtToImg,
        HomeNavigation.ImgToImg,
        HomeNavigation.Gallery,
        HomeNavigation.Settings,
    )

    fun lora(alias: String) = "<lora:$alias:1>"
}
