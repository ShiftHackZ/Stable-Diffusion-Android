package com.shifthackz.aisdv1.presentation.utils

object Constants {
    const val PAGINATION_PAYLOAD_SIZE = 1000
    const val DEBUG_MENU_ACCESS_TAPS = 7

    const val PARAM_ITEM_ID = "itemId"
    const val PARAM_SOURCE = "source"

    const val ROUTE_SPLASH = "splash"
    const val ROUTE_SERVER_SETUP = "server_setup"
    const val ROUTE_SERVER_SETUP_FULL = "$ROUTE_SERVER_SETUP/{$PARAM_SOURCE}"
    const val ROUTE_CONFIG_LOADER = "config_loader"
    const val ROUTE_HOME = "home"
    const val ROUTE_WEB_UI = "webui"
    const val ROUTE_TXT_TO_IMG = "text_to_image"
    const val ROUTE_IMG_TO_IMG = "image_to_image"
    const val ROUTE_GALLERY = "gallery"
    const val ROUTE_GALLERY_DETAIL = "gallery_detail"
    const val ROUTE_GALLERY_DETAIL_FULL = "$ROUTE_GALLERY_DETAIL/{$PARAM_ITEM_ID}"
    const val ROUTE_SETTINGS = "settings"
    const val ROUTE_DEBUG = "debug"
    const val ROUTE_LOGGER = "logger"
    const val ROUTE_IN_PAINT = "in_paint"
    const val ROUTE_DONATE = "donate"

    const val SUB_SEED_STRENGTH_MIN = 0f
    const val SUB_SEED_STRENGTH_MAX = 1f

    const val SAMPLING_STEPS_RANGE_MIN = 10
    const val SAMPLING_STEPS_RANGE_MAX = 150
    const val SAMPLING_STEPS_RANGE_STABILITY_AI_MAX = 50

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
        ROUTE_TXT_TO_IMG,
        ROUTE_IMG_TO_IMG,
        ROUTE_GALLERY,
        ROUTE_SETTINGS
    )

    fun lora(alias: String) = "<lora:$alias:1>"
}
